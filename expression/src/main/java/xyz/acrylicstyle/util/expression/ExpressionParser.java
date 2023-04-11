package xyz.acrylicstyle.util.expression;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.StringReader;
import xyz.acrylicstyle.util.expression.instruction.DummyInstTypeInfo;
import xyz.acrylicstyle.util.expression.instruction.InstGetField;
import xyz.acrylicstyle.util.expression.instruction.InstInvokeVirtual;
import xyz.acrylicstyle.util.expression.instruction.InstLoadVariable;
import xyz.acrylicstyle.util.expression.instruction.InstStoreDouble;
import xyz.acrylicstyle.util.expression.instruction.InstStoreFloat;
import xyz.acrylicstyle.util.expression.instruction.InstStoreInt;
import xyz.acrylicstyle.util.expression.instruction.InstStoreLong;
import xyz.acrylicstyle.util.expression.instruction.InstStoreString;
import xyz.acrylicstyle.util.expression.instruction.InstructionSet;
import xyz.acrylicstyle.util.expression.util.AbstractFinder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class ExpressionParser {
    @Contract("_, _ -> new")
    public static @NotNull InstructionSet compile(@NotNull String source, @NotNull CompileData compileData) throws InvalidArgumentException {
        return compile(new InstructionSet(), StringReader.create(source), compileData);
    }

    public static @NotNull InstructionSet compile(@NotNull InstructionSet instructionSet, @NotNull StringReader source, @NotNull CompileData compileData) throws InvalidArgumentException {
        source.skipWhitespace();
        if (source.peek() == '{') {
            source.skip();
            compile(instructionSet, source, compileData);
        }
        while (!source.isEOF()) {
            if (source.peek() == '"') {
                String quoted = source.readQuotedString('\n', '\r', '\t');
                instructionSet.add(new InstStoreString(quoted));
                instructionSet.add(new DummyInstTypeInfo(String.class));
                if (!source.isEOF()) source.skip();
                continue;
            }
            String token = source.readUntil(' ', '.', ',', '(', ')', '{', '}', '|', ':', ';', '=', '"', '\'', '\n', '\r');
            if (token.isEmpty()) {
                throw InvalidArgumentException.expected("token", "").withContext(source);
            }
            if (!source.isEOF()) source.skip();
            source.skipWhitespace();
            if (!source.isEOF() && source.peek(-1) == '(') {
                int methodIndex = source.index() - 1 - token.length();
                Class<?> type;
                if (instructionSet.lastOrNull() instanceof DummyInstTypeInfo) {
                    type = ((DummyInstTypeInfo) Objects.requireNonNull(instructionSet.lastOrNull())).getClazz();
                } else {
                    throw new AssertionError("type info is missing: " + instructionSet);
                }
                List<Class<?>> args = new ArrayList<>();
                while (!source.peekEquals(')')) {
                    compile(instructionSet, source, compileData);
                    source.skip(-1);
                    if (instructionSet.lastOrNull() instanceof DummyInstTypeInfo) {
                        Class<?> clazz = ((DummyInstTypeInfo) Objects.requireNonNull(instructionSet.lastOrNull())).getClazz();
                        args.add(clazz);
                    } else {
                        throw new AssertionError("type info is missing");
                    }
                    source.skipWhitespace();
                    if (!source.isEOF() && (source.peekEquals(',') || source.peekEquals(')'))) {
                        source.skip();
                        if (source.peek(-1) == ')') {
                            break;
                        }
                    } else {
                        throw InvalidArgumentException.expected("',' or ')'", Character.toString(source.peek())).withContext(source);
                    }
                }
                if (!source.isEOF()) source.skip();
                if (!source.isEOF() && source.peek() == '.') source.skip();
                try {
                    resolveMethod(instructionSet, type, token, args);
                    continue;
                } catch (NoSuchMethodException e) {
                    throw new InvalidArgumentException("No such method " + token + " in " + type.getTypeName() + " (args: " + args + ")")
                            .withContext(source, methodIndex - source.index(), source.index() - methodIndex)
                            .withCause(e);
                }
            }
            resolveToken(instructionSet, source, compileData, token);
        }
        return instructionSet;
    }

    private static void resolveToken(@NotNull InstructionSet instructionSet, @NotNull StringReader source, @NotNull CompileData compileData, @NotNull String token) throws InvalidArgumentException {
        if (source.index() > token.length() + 1 && source.peek(-token.length() - 2) == '.') {
            if (instructionSet.lastOrNull() instanceof DummyInstTypeInfo) {
                Class<?> clazz = ((DummyInstTypeInfo) Objects.requireNonNull(instructionSet.lastOrNull())).getClazz();
                resolveToken(instructionSet, clazz, token);
            } else {
                throw new AssertionError("type info is missing");
            }
        } else {
            try {
                int i = Integer.parseInt(token);
                instructionSet.add(new InstStoreInt(i));
                instructionSet.add(new DummyInstTypeInfo(Integer.class));
                return;
            } catch (NumberFormatException ignored) {}
            try {
                if (token.endsWith("L") || token.endsWith("l")) {
                    token = token.substring(0, token.length() - 1);
                }
                long l = Long.parseLong(token);
                instructionSet.add(new InstStoreLong(l));
                instructionSet.add(new DummyInstTypeInfo(Long.class));
                return;
            } catch (NumberFormatException ignored) {}
            try {
                if (token.endsWith("F") || token.endsWith("f")) {
                    token = token.substring(0, token.length() - 1);
                }
                float f = Float.parseFloat(token);
                instructionSet.add(new InstStoreFloat(f));
                instructionSet.add(new DummyInstTypeInfo(Float.class));
                return;
            } catch (NumberFormatException ignored) {}
            try {
                if (token.endsWith("D") || token.endsWith("d")) {
                    token = token.substring(0, token.length() - 1);
                }
                double d = Double.parseDouble(token);
                instructionSet.add(new InstStoreDouble(d));
                instructionSet.add(new DummyInstTypeInfo(Double.class));
                return;
            } catch (NumberFormatException ignored) {}
            Class<?> type = compileData.getVariable(token);
            instructionSet.add(new InstStoreString(token));
            instructionSet.add(new InstLoadVariable());
            instructionSet.add(new DummyInstTypeInfo(type));
        }
    }

    private static void resolveToken(@NotNull InstructionSet instructionSet, @NotNull Class<?> type, @NotNull String token) {
        String capitalizedSymbol = token.substring(0, 1).toUpperCase(Locale.ROOT) + token.substring(1);
        for (Class<?> clazz : getSupers(type)) {
            for (Method method : clazz.getMethods()) {
                if ((method.getName().equals(token) || method.getName().equals("get" + capitalizedSymbol)) && method.getParameterCount() == 0) {
                    instructionSet.add(new InstInvokeVirtual(method));
                    instructionSet.add(new DummyInstTypeInfo(method.getReturnType()));
                    return;
                }
            }
            for (Field field : clazz.getFields()) {
                if (field.getName().equals(token)) {
                    instructionSet.add(new InstGetField(field));
                    instructionSet.add(new DummyInstTypeInfo(field.getType()));
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Could not resolve " + token + " in " + type.getTypeName());
    }

    private static void resolveMethod(@NotNull InstructionSet instructionSet, @NotNull Class<?> type, @NotNull String name, @NotNull List<Class<?>> args) throws NoSuchMethodException {
        AbstractFinder<Method> finder = new AbstractFinder<>(name, args.toArray(new Class[0]));
        Set<Method> methods = new LinkedHashSet<>();
        for (Class<?> clazz : getSupers(type)) {
            methods.addAll(Arrays.asList(clazz.getMethods()));
        }
        Method method = finder.find(methods.toArray(new Method[0]));
        instructionSet.add(new InstInvokeVirtual(method));
        instructionSet.add(new DummyInstTypeInfo(method.getReturnType()));
    }

    private static @NotNull Set<@NotNull Class<?>> getSupers(@NotNull Class<?> type) {
        Set<Class<?>> list = new LinkedHashSet<>();
        list.add(type);
        if (type.getSuperclass() != null) {
            list.add(type.getSuperclass());
            list.addAll(getSupers(type.getSuperclass()));
        }
        list.addAll(Arrays.asList(type.getInterfaces()));
        for (Class<?> anInterface : type.getInterfaces()) {
            list.addAll(getSupers(anInterface));
        }
        return list;
    }
}
