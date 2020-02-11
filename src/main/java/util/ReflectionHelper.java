package util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Helps you using reflection.
 */
public final class ReflectionHelper {
    private ReflectionHelper() {}

    /**
     * Find method in class.
     * @param clazz Class that will find method on
     * @param methodName Method name
     * @param args Class of arguments
     * @return Method if found, null otherwiwise
     */
    public static <T> Method findMethod(Class<? extends T> clazz, String methodName, Class<?>... args) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Invokes method.
     * @param clazz Class that will invoke method on
     * @param instance Can be empty if static method
     * @param methodName Method name
     * @param args Arguments
     * @return Result of method
     * @throws InvocationTargetException If something went wrong when invoking method
     * @throws IllegalAccessException If invocation isn't allowed
     * @throws NoSuchMethodException If couldn't method find
     */
    public static <T> Object invokeMethod(Class<? extends T> clazz, T instance, String methodName, Object... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<Class<?>> classes = new ArrayList<>();
        for (Object arg : args) classes.add(arg.getClass());
        Method method = findMethod(clazz, methodName, classes.toArray(new Class[0]));
        if (method == null) throw new NoSuchMethodException();
        return method.invoke(instance, args);
    }

    /**
     * Find field in class.
     * @param clazz Class that will find field on
     * @param fieldName Field name
     * @return Field if found, null otherwise
     */
    public static <T> Field findField(Class<? extends T> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Get value in field in class.
     * @param clazz Class that will get field on
     * @param instance Can be empty if field is static
     * @param fieldName Field name
     * @return Value of field
     * @throws NoSuchFieldException If couldn't find field
     * @throws IllegalAccessException If the operation isn't allowed
     */
    public static <T> Object getField(Class<? extends T> clazz, T instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(clazz, fieldName);
        if (field == null) throw new NoSuchFieldException();
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * Get value in field in class.
     * @param clazz Class that will get field on
     * @param instance Can be empty if field is static
     * @param fieldName Field name
     * @return Value of field if success, null otherwise
     */
    public static <T> Object getFieldWithoutException(Class<? extends T> clazz, T instance, String fieldName) {
        try {
            return getField(clazz, instance, fieldName);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return null;
        }
    }

    /**
     * Set value in field in class.
     * @param clazz Class that will get field on
     * @param instance Can be empty if field is static
     * @param fieldName Field name
     * @param value Value
     * @throws NoSuchFieldException If couldn't find field
     * @throws IllegalAccessException If the operation isn't allowed
     */
    public static <T> void setField(Class<? extends T> clazz, T instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(clazz, fieldName);
        if (field == null) throw new NoSuchFieldException();
        field.setAccessible(true);
        field.set(instance, value);
    }

    /**
     * Set value in field in class.
     * @param clazz Class that will get field on
     * @param instance Can be empty if field is static
     * @param fieldName Field name
     * @param value Value
     * @return True if success, false otherwise
     */
    public static <T> boolean setFieldWithoutException(Class<? extends T> clazz, T instance, String fieldName, Object value) {
        try {
            setField(clazz, instance, fieldName, value);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return false;
        }
    }

    /**
     * Find constructor in class.
     * @param clazz Class that will find constructor on
     * @param types Parameter Types
     * @return Constructor if found, null otherwise
     */
    public static <T> Constructor<? super T> findConstructor(Class<T> clazz, Class<?>... types) {
        try {
            Constructor<? super T> constructor = clazz.getConstructor(types);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Invoke constructor.
     * @param clazz Class that will invoke constructor on
     * @param args Arguments for invoke constructor
     * @return Result of constructor
     * @throws InvocationTargetException If something went wrong when invoking method
     * @throws IllegalAccessException If invocation isn't allowed
     * @throws InstantiationException If can't be initialized
     */
    public static <T> Object invokeConstructor(Class<T> clazz, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Class<?>> classes = new ArrayList<>();
        for (Object arg : args) classes.add(arg.getClass());
        Constructor<? super T> constructor = findConstructor(clazz, classes.toArray(new Class[0]));
        if (constructor == null) throw new NoSuchMethodError();
        return constructor.newInstance(args);
    }

    /**
     * Invoke constructor.
     * @param clazz Class that will invoke constructor on
     * @param args Arguments for invoke constructor
     * @return Result of constructor if success, null otherwise
     */
    public static <T> Object invokeConstructorWithoutException(Class<? extends T> clazz, Object... args) {
        try {
            return invokeConstructor(clazz, args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ignored) {
            return null;
        }
    }
}
