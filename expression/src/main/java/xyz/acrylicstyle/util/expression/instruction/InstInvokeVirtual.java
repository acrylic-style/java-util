package xyz.acrylicstyle.util.expression.instruction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.expression.Opcodes;
import xyz.acrylicstyle.util.expression.RuntimeData;
import xyz.acrylicstyle.util.expression.util.ReflectionUtil;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * <p>Arguments:</p>
 * <ul>
 *     <li>instance</li>
 *     <li><i>args...</i></li>
 * </ul>
 * <p>Returns: {@link Object} (value returned from method)</p>
 */
public class InstInvokeVirtual extends Instruction {
    private String clazz;
    private String name;
    private String desc;

    public InstInvokeVirtual() {
    }

    public InstInvokeVirtual(@NotNull Method method) {
        this.clazz = method.getDeclaringClass().getTypeName();
        this.name = method.getName();
        this.desc = ReflectionUtil.desc(method);
    }

    public InstInvokeVirtual(@Nullable String clazz, @Nullable String name, @Nullable String desc) {
        super();
        this.clazz = clazz;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void load(@NotNull ByteBuffer buf) {
        this.clazz = getString(buf);
        this.name = getString(buf);
        this.desc = getString(buf);
    }

    @Override
    public void save(@NotNull ByteBuffer buf) {
        putString(buf, clazz);
        putString(buf, name);
        putString(buf, desc);
    }

    @Override
    public Object execute(@NotNull RuntimeData runtimeData, @NotNull Deque<Object> stack) {
        try {
            Class<?> clazz = Class.forName(this.clazz);
            MethodType type = MethodType.fromMethodDescriptorString(desc, InstInvokeVirtual.class.getClassLoader());
            List<Object> args = new ArrayList<>();
            for (int i = 0; i < type.parameterCount(); i++) {
                args.add(stack.removeLast());
            }
            Collections.reverse(args);
            Object instance = stack.removeLast();
            if (runtimeData.isAllowPrivate()) {
                Method method = clazz.getDeclaredMethod(name, type.parameterArray());
                if (!Modifier.isPublic(method.getModifiers())) {
                    method.setAccessible(true);
                }
                return method.invoke(instance, args.toArray());
            } else {
                return MethodHandles.publicLookup()
                        .findVirtual(clazz, name, type)
                        .bindTo(instance)
                        .invokeWithArguments(args.toArray());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte getId() {
        return Opcodes.INVOKE_VIRTUAL;
    }

    @Override
    public String toString() {
        return "InstInvokeVirtual{" +
                "clazz='" + clazz + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
