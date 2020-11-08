package util.reflector;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ReflectionHelper;
import util.Validate;
import util.reflect.Ref;
import util.reflect.RefField;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReference;

public class ReflectorHandler implements InvocationHandler {
    private final Class<?> target;
    private final Object instance;

    @NotNull
    public Class<?> getTarget() {
        return target;
    }

    public Object getInstance() { return instance; }

    public ReflectorHandler(@NotNull Class<?> target, @NotNull Object instance) {
        Validate.notNull(target, "target cannot be null");
        Validate.notNull(instance, "instance cannot be null");
        this.target = target;
        this.instance = instance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args_) throws Throwable {
        Object[] args = parseFieldGetterParameter(method, args_);
        if (method.isDefault()) System.err.println("Default methods are not supported. Please don't mark methods as 'default'. Method: " + method.toGenericString());
        if (method.equals(ClazzGetter.METHOD) && (args == null || args.length == 0)) {
            return Object.class.getMethod("getClass").invoke(instance);
        }
        FieldGetter getter = method.getAnnotation(FieldGetter.class);
        FieldSetter setter = method.getAnnotation(FieldSetter.class);
        ForwardMethod forwardMethod = method.getAnnotation(ForwardMethod.class);
        CastTo castTo = method.getAnnotation(CastTo.class);
        if (getter != null) {
            if (args != null && args.length > 0) throw new IllegalArgumentException("Requires exactly zero argument on method when applying FieldGetter");
            return getField(instance, proxy, getter, castTo, target, method);
        }
        if (setter != null) {
            if ((args == null || args.length == 0) || (args.length > 1)) throw new IllegalArgumentException("Requires exactly one argument on method when applying FieldSetter");
            setField(instance, setter, target, method, args[0]);
            return null;
        }
        String methodName = forwardMethod == null ? method.getName() : forwardMethod.value();
        Method found = findMethod(target, methodName, method.getParameterTypes());
        if (found != null) {
            System.out.println("aFound: " + found.toGenericString() + ", instance: " + instance);
            if (castTo != null) {
                if (castTo.createInstance()) {
                    return castTo.value().getConstructor(Object.class).newInstance(found.invoke(Reflector.reverseInstanceList.getOrDefault(instance, instance), args));
                } else {
                    return Reflector.castTo(null, method.getDeclaringClass(), proxy, methodName, method, castTo.value(), args);
                }
            }
            try {
                return found.invoke(instance, args);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                return found.invoke(Reflector.reverseInstanceList.getOrDefault(instance, instance), args);
            }
        } else {
            if (method.getName().startsWith("get") && method.getName().length() >= 4 && (args == null || args.length == 0)) {
                return getField(instance, proxy, null, castTo, target, method);
            } else if (method.getName().startsWith("set") && method.getName().length() >= 4 && args.length == 1) {
                setField(instance, null, target, method, args[0]);
                return null;
            }
            throw new NoSuchMethodException(method.toGenericString());
        }
    }

    private static Object getField(@NotNull Object instance,
                                   @NotNull Object proxy,
                                   @Nullable FieldGetter getter,
                                   @Nullable CastTo castTo,
                                   @NotNull Class<?> target,
                                   @NotNull Method method) throws Throwable {
        Field field = getField(getter == null ? null : getter.value(), target, method);
        if (castTo != null) {
            if (castTo.createInstance()) {
                return castTo.value().getConstructor(Object.class).newInstance(field.get(instance));
            } else {
                return Reflector.castFieldTo(method.getDeclaringClass(), proxy, field.getName(), castTo.value());
            }
        }
        return field.get(instance);
    }

    private static void setField(@NotNull Object instance, @Nullable FieldSetter setter, @NotNull Class<?> target, @NotNull Method method, @Nullable Object arg) throws NoSuchFieldException {
        Field field = getField(setter == null ? null : setter.value(), target, method);
        RefField<?> refField = new RefField<>(field);
        if (setter != null && setter.removeFinal()) refField.removeFinal();
        refField.setObj(Reflector.reverseInstanceList.get(instance), arg);
    }

    @NotNull
    private static Field getField(String value, Class<?> target, Method method) throws NoSuchFieldException {
        Field field;
        if (value == null || value.equals("")) {
            field = findField(target, method);
        } else {
            field = findField(target, value);
        }
        if (field == null) throw new NoSuchFieldException(value == null || value.equals("") ? fieldName(method) : value);
        return field;
    }

    @Contract("_, _ -> param2")
    private static Object[] parseFieldGetterParameter(@NotNull Method method, @Nullable Object[] args) throws Throwable {
        if (args == null) return null;
        for (int i = 0; i < method.getParameters().length; i++) {
            Object arg = args[i];
            if (arg == null) return null;
            FieldGetter getter = method.getParameters()[i].getAnnotation(FieldGetter.class);
            ForwardMethod forwardMethod = method.getParameters()[i].getAnnotation(ForwardMethod.class);
            if (getter == null && forwardMethod == null) continue;
            Class<?> target = arg.getClass();
            if (getter != null) {
                if (!getter.target().equals(Object.class)) target = getter.target();
                Field field = findField(target, getter.value());
                if (field == null) throw new NoSuchFieldException("Could not find field " + target.getCanonicalName() + "#" + getter.value());
                args[i] = field.get(arg);
            }
            if (forwardMethod != null) {
                if (!forwardMethod.target().equals(Object.class)) target = forwardMethod.target();
                Method m = findMethod(target, forwardMethod.value());
                if (m == null) throw new NoSuchMethodException("Could not find method " + target.getCanonicalName() + "#" + forwardMethod.value());
                args[i] = m.invoke(arg);
            }
        }
        return args;
    }

    @NotNull
    private static String fieldName(Method method) { return deCapitalize(method.getName().replaceFirst("[gs]et", "")); }

    @Nullable
    private static Field findField(Class<?> target, Method method) {
        return findField(target, fieldName(method));
    }

    @Nullable
    private static <T> Method findMethod(@NotNull Class<? extends T> clazz, @NotNull String methodName, @Nullable Class<?>... args) {
        AtomicReference<Method> method = new AtomicReference<>();
        AtomicReference<Method> implMethod = new AtomicReference<>();
        ReflectionHelper.getSupers(clazz).addChain(clazz).forEach(cl -> {
            try {
                Method m = cl.getDeclaredMethod(methodName, args);
                if (m.isDefault() || (m.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) implMethod.set(m);
                method.set(m);
            } catch (NoSuchMethodException ignore) {}
        });
        if (implMethod.get() == null) implMethod.set(method.get());
        if (implMethod.get() != null) implMethod.get().setAccessible(true);
        return implMethod.get();
    }

    @Contract(pure = true)
    @Nullable
    private static <T> Field findField(@NotNull Class<? extends T> clazz, @NotNull String fieldName) {
        AtomicReference<Field> field = new AtomicReference<>();
        ReflectionHelper.getSupers(clazz).forEach(cl -> {
            try {
                field.set(cl.getDeclaredField(fieldName));
            } catch (NoSuchFieldException ignore) {}
        });
        if (field.get() != null) field.get().setAccessible(true);
        return field.get();
    }

    @NotNull
    private static String deCapitalize(String s) { return s.substring(0, 1).toLowerCase() + s.substring(1); }

    public interface ClazzGetter {
        Method METHOD = Ref.getMethod(ClazzGetter.class, "getClazz").getMethod();

        Class<?> getClazz();
    }
}
