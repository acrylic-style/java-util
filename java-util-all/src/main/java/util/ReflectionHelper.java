package util;

import com.google.common.reflect.ClassPath;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import util.reflect.Ref;
import util.reflect.RefMethod;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Helps you using reflection.
 */
public final class ReflectionHelper {
    private ReflectionHelper() {}

    public static final RefMethod<ClassLoader> getPackagesMethod = Ref.getDeclaredMethod(ClassLoader.class, "getPackages").accessible(true);
    public static final RefMethod<ClassLoader> getPackageMethod = Ref.getDeclaredMethod(ClassLoader.class, "getPackage", String.class).accessible(true);

    /**
     * Find method in class.
     * @param clazz Class that will find method on
     * @param methodName Method name
     * @param args Class of arguments
     * @return Method if found, null otherwise
     */
    @Nullable
    public static <T> Method findMethod(@NotNull Class<? extends T> clazz, @NotNull String methodName, @Nullable Class<?>... args) {
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
    public static <T> Object invokeMethod(@NotNull Class<? extends T> clazz, @Nullable T instance, @NotNull String methodName, @NotNull Object... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<Class<?>> classes = new ArrayList<>();
        for (Object arg : args) classes.add(arg.getClass());
        Method method = findMethod(clazz, methodName, classes.toArray(new Class[0]));
        if (method == null) throw new NoSuchMethodException();
        return method.invoke(instance, args);
    }

    /**
     * Invokes method.
     * @param clazz Class that will invoke method on
     * @param instance Can be empty if static method
     * @param methodName Method name
     * @param args Arguments
     * @return Result of method, null if invoked method returned null or thrown error
     */
    public static <T> Object invokeMethodWithoutException(@NotNull Class<? extends T> clazz, @Nullable T instance, @NotNull String methodName, @NotNull Object... args) {
        try {
            return invokeMethod(clazz, instance, methodName, args);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find field in class.
     * @param clazz Class that will find field on
     * @param fieldName Field name
     * @return Field if found, null otherwise
     */
    @Nullable
    public static <T> Field findField(@NotNull Class<? extends T> clazz, @NotNull String fieldName) {
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
    @NotNull
    public static <T> Object getField(@NotNull Class<? extends T> clazz, @Nullable T instance, @NotNull String fieldName) throws NoSuchFieldException, IllegalAccessException {
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
    @Nullable
    public static <T> Object getFieldWithoutException(@NotNull Class<? extends T> clazz, @Nullable T instance, @NotNull String fieldName) {
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
    public static <T> void setField(@NotNull Class<? extends T> clazz, @Nullable T instance, @NotNull String fieldName, @Nullable Object value) throws NoSuchFieldException, IllegalAccessException {
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
    public static <T> boolean setFieldWithoutException(@NotNull Class<? extends T> clazz, @Nullable T instance, @NotNull String fieldName, @Nullable Object value) {
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
    @Nullable
    public static <T> Constructor<? super T> findConstructor(@NotNull Class<T> clazz, @Nullable Class<?>... types) {
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
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> T invokeConstructor(@NotNull Class<T> clazz, @NotNull Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Class<?>> classes = new ArrayList<>();
        for (Object arg : args) classes.add(arg.getClass());
        Constructor<? super T> constructor = findConstructor(clazz, classes.toArray(new Class[0]));
        if (constructor == null) throw new NoSuchMethodError();
        return (T) constructor.newInstance(args);
    }

    /**
     * Invoke constructor.
     * @param clazz Class that will invoke constructor on
     * @param args Arguments for invoke constructor
     * @return Result of constructor if success, null otherwise
     */
    @Nullable
    public static <T> T invokeConstructorWithoutException(@NotNull Class<? extends T> clazz, @NotNull Object... args) {
        try {
            return invokeConstructor(clazz, args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ignored) {
            return null;
        }
    }

    @NotNull
    @Contract("_, _ -> new")
    public static CollectionList<Class<?>> findAllAnnotatedClasses(@NotNull String packageName, @NotNull Class<? extends Annotation> annotation) {
        CollectionList<Class<?>> classes = new CollectionList<>();
        classes.addAll(new Reflections(packageName).getTypesAnnotatedWith(annotation));
        return classes;
    }

    @NotNull
    public static CollectionList<Class<?>> findAllAnnotatedClasses(@Nullable ClassLoader classLoader, @NotNull String packageName, @NotNull Class<? extends Annotation> annotation) {
        CollectionList<Class<?>> classes = new CollectionList<>();
        classes.addAll(new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forClassLoader(classLoader))
                        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName)))
        ).getTypesAnnotatedWith(annotation));
        return classes;
    }

    @NotNull
    public static CollectionList<Class<?>> findAllClasses(@Nullable ClassLoader classLoader, @NotNull String packageName, boolean recursive) {
        if (recursive) return findAllClassesRecursive(classLoader, packageName);
        return findAllClasses(classLoader, packageName);
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    public static CollectionList<Class<?>> findAllClasses(@Nullable ClassLoader classLoader, @NotNull String packageName) {
        try {
            return new CollectionList<>(ClassPath.from(classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader).getTopLevelClasses(packageName))
                    .map(ClassPath.ClassInfo::load);
        } catch (IOException e) {
            SneakyThrow.sneaky(e);
            return null;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    public static CollectionList<Class<?>> findAllClassesRecursive(@Nullable ClassLoader classLoader, @NotNull String packageName) {
        try {
            return new CollectionList<>(ClassPath.from(classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader).getTopLevelClassesRecursive(packageName))
                    .map(ClassPath.ClassInfo::load);
        } catch (IOException e) {
            SneakyThrow.sneaky(e);
            return null;
        }
    }

    @NotNull
    public static ICollectionList<String> findPackages(@NotNull String prefix, boolean recursive) {
        return ICollectionList.asList(Package.getPackages())
                .map(Package::getName)
                .filter(s -> {
                    if (recursive) return s.toLowerCase().startsWith(prefix.toLowerCase());
                    String c = s.split("\\.")[s.split("\\.").length-1];
                    return s.equalsIgnoreCase(prefix + "." + c);
                });
    }

    @NotNull
    public static ICollectionList<String> findPackages(@Nullable ClassLoader cl, @NotNull String prefix, boolean recursive) {
        if (cl == null) return findPackages(prefix, recursive);
        return ICollectionList.asList((Package[]) getPackagesMethod.invoke(cl))
                .map(Package::getName)
                .filter(s -> {
                    if (recursive) return s.toLowerCase().startsWith(prefix.toLowerCase());
                    String c = s.split("\\.")[s.split("\\.").length-1];
                    return s.equalsIgnoreCase(prefix + "." + c);
                });
    }

    public static boolean isValidPackage(@NotNull String packageName) {
        return Package.getPackage(packageName) != null;
    }

    public static boolean isValidPackage(@Nullable ClassLoader cl, @NotNull String packageName) {
        if (cl == null) return isValidPackage(packageName);
        return getPackageMethod.invoke(cl, packageName) != null;
    }

    @NotNull
    public static Class<?> getCallerClass() { return getCallerClass(3); } // 2 + this method

    @NotNull
    public static Class<?> getCallerClass(int offset) {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1 + offset; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ReflectionHelper.class.getName()) && !ste.getClassName().contains("java.lang.Thread")) {
                return Ref.forName(ste.getClassName()).getClazz();
            }
        }
        throw new NoSuchElementException("sorry :(");
    }

    /**
     * Gets all super classes and super interfaces, and return them. The returned entry is not unique and may contains the duplicate entry.
     * @return the super classes and interfaces.
     */
    public static ICollectionList<Class<?>> getSupers(Class<?> clazz) {
        return getSuperclasses(clazz).concat(getInterfaces(clazz));
    }

    public static CollectionList<Class<?>> getSuperclasses(Class<?> clazz) {
        CollectionList<Class<?>> classes = new CollectionList<>();
        Class<?> superclass = clazz;
        while (superclass.getSuperclass() != null) {
            classes.add(superclass.getSuperclass());
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    public static CollectionList<Class<?>> getInterfaces(Class<?> clazz) {
        CollectionList<Class<?>> classes = new CollectionList<>(clazz);
        for (Class<?> anInterface : clazz.getInterfaces()) classes.addAll(getInterfaces(anInterface));
        Class<?> superclass = clazz;
        while (superclass.getSuperclass() != null) {
            classes.addAll(getInterfaces(superclass.getSuperclass()));
            superclass = superclass.getSuperclass();
        }
        return classes;
    }
}
