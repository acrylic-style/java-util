package util.reflector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Validate;
import util.reflect.Ref;
import util.reflect.RefField;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) System.err.println("Default methods are not supported. Please don't mark methods as 'default'. Method: " + method.toGenericString());
        if (method.equals(ClazzGetter.METHOD) && (args == null || args.length == 0)) {
            return Object.class.getMethod("getClass").invoke(instance);
        }
        FieldGetter getter = method.getAnnotation(FieldGetter.class);
        FieldSetter setter = method.getAnnotation(FieldSetter.class);
        ForwardMethod forwardMethod = method.getAnnotation(ForwardMethod.class);
        if (getter != null) {
            if (args != null && args.length > 0) throw new IllegalArgumentException("Requires exactly zero argument on method when applying FieldGetter");
            Field field;
            if (getter.value().equals("")) {
                field = findField(target, method);
            } else {
                field = findField(target, getter.value());
            }
            if (field == null) throw new NoSuchFieldException(getter.value().equals("") ? fieldName(method) : getter.value());
            return field.get(instance);
        }
        if (setter != null) {
            if ((args == null || args.length == 0) || (args.length > 1)) throw new IllegalArgumentException("Requires exactly one argument on method when applying FieldSetter");
            Field field;
            if (setter.value().equals("")) {
                field = findField(target, method);
            } else {
                field = findField(target, setter.value());
            }
            if (field == null) throw new NoSuchFieldException(setter.value().equals("") ? fieldName(method) : setter.value());
            RefField<?> refField = new RefField<>(field);
            if (setter.removeFinal()) refField.removeFinal();
            refField.setObj(instance, args[0]);
            return null;
        }
        String methodName = method.getName();
        if (forwardMethod != null) {
            methodName = forwardMethod.value();
        }
        Method found = findMethod(target, methodName, method.getParameterTypes());
        if (found != null) {
            return found.invoke(instance, args);
        } else {
            if (method.getName().startsWith("get") && method.getName().length() >= 4 && (args == null || args.length == 0)) {
                Field field = findField(target, method);
                if (field != null) return field.get(instance);
            } else if (method.getName().startsWith("set") && method.getName().length() >= 4 && args.length == 1) {
                Field field = findField(target, method);
                if (field != null) {
                    field.set(instance, args[0]);
                    return null;
                }
            }
            throw new NoSuchMethodException(method.toGenericString());
        }
    }

    public static String fieldName(Method method) { return deCapitalize(method.getName().replaceFirst("[gs]et", "")); }

    public static Field findField(Class<?> target, Method method) {
        return findField(target, fieldName(method));
    }

    public static <T> Method findMethod(@NotNull Class<? extends T> clazz, @NotNull String methodName, @Nullable Class<?>... args) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> Field findField(@NotNull Class<? extends T> clazz, @NotNull String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static String deCapitalize(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public interface ClazzGetter {
        Method METHOD = Ref.getMethod(ClazzGetter.class, "getClazz").getMethod();

        Class<?> getClazz();
    }
}
