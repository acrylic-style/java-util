package util.reflect;

import com.google.common.reflect.ClassPath;
import net.blueberrymc.native_util.NativeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.FilterBuilder;
import util.base.Lists;
import util.base.Throwables;
import util.ReflectionsConfigurationBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helps you using reflection.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ReflectionHelper {
    private ReflectionHelper() {}

    // Java 8
    private static final @NotNull Optional<Method> getPackagesMethod = NativeUtil.getMethodOptional(ClassLoader.class, "getPackages", "()[Ljava/lang/Package;");
    private static final @NotNull Optional<Method> getPackageMethod = NativeUtil.getMethodOptional(ClassLoader.class, "getPackage", "(Ljava/lang/String;)Ljava/lang/Package;");

    // Java 9+
    private static final @NotNull Optional<Method> getDefinedPackagesMethod = NativeUtil.getMethodOptional(ClassLoader.class, "getDefinedPackages", "()Ljava/lang/Package;");
    private static final @NotNull Optional<Method> getDefinedPackageMethod = NativeUtil.getMethodOptional(ClassLoader.class, "getDefinedPackage", "(Ljava/lang/String;)Ljava/lang/Package;");

    @Nullable
    public static Package getPackage(@NotNull("classLoader") ClassLoader cl, @NotNull("name") String name) {
        Method method = getDefinedPackageMethod.orElse(getPackageMethod.orElseThrow(NoSuchElementException::new));
        return (Package) NativeUtil.invoke(method, cl, name);
    }

    @NotNull
    public static Package@NotNull[] getPackages(@NotNull("classLoader") ClassLoader cl) {
        Method method = getDefinedPackagesMethod.orElse(getPackagesMethod.orElseThrow(NoSuchElementException::new));
        return (Package[]) NativeUtil.invoke(method, cl);
    }

    @NotNull
    public static Class<?> forName(String className) throws RuntimeException {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Find method in class.
     * @param clazz Class that will find method on
     * @param methodName Method name
     * @param args Class of arguments
     * @return Method if found, null otherwise
     */
    @Nullable
    public static <T> Method findMethod(@NotNull("clazz") Class<? extends T> clazz, @NotNull("methodName") String methodName, @Nullable Class<?>... args) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Nullable
    public static <T> Method findMethodRecursively(@NotNull("clazz") Class<? extends T> clazz, @NotNull("methodName") String methodName, @Nullable Class<?>... args) {
        Method m = findMethod(clazz, methodName, args);
        if (m != null) return m;
        for (Class<?> cl : getSupers(clazz)) {
            try {
                Method method = cl.getDeclaredMethod(methodName, args);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignore) {}
        }
        return null;
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
    public static <T> Object invokeMethod(@NotNull("clazz") Class<? extends T> clazz, @Nullable T instance, @NotNull("methodName") String methodName, @NotNull("args") Object... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = findMethod(clazz, methodName, getClassesForParams(args));
        if (method == null) throw new NoSuchMethodException();
        return method.invoke(instance, args);
    }

    public static Class<?>[] getClassesForParams(Object... args) {
        List<Class<?>> classes = new ArrayList<>();
        for (Object arg : args) classes.add(arg.getClass());
        return classes.toArray(new Class[0]);
    }

    public static <T> Object invokeMethodRecursively(@NotNull("clazz") Class<? extends T> clazz, @Nullable T instance, @NotNull("methodName") String methodName, @NotNull("args") Object... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<Class<?>> classes = new ArrayList<>();
        for (Object arg : args) classes.add(arg.getClass());
        Method method = findMethodRecursively(clazz, methodName, classes.toArray(new Class[0]));
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
    public static <T> Object invokeMethodWithoutException(@NotNull("clazz") Class<? extends T> clazz, @Nullable T instance, @NotNull("methodName") String methodName, @NotNull("args") Object... args) {
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
    public static <T> Field findField(@NotNull("clazz") Class<? extends T> clazz, @NotNull("field") String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    @Nullable
    public static <T> Field findFieldRecursively(@NotNull("clazz") Class<? extends T> clazz, @NotNull("field") String fieldName) {
        Field f = findField(clazz, fieldName);
        if (f != null) return f;
        for (Class<?> cl : getSupers(clazz)) {
            try {
                Field field = cl.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignore) {}
        }
        return null;
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
    public static <T> Object getField(@NotNull("clazz") Class<? extends T> clazz, @Nullable T instance, @NotNull("field") String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = findField(clazz, fieldName);
        if (field == null) throw new NoSuchFieldException();
        field.setAccessible(true);
        return field.get(instance);
    }

    public static <T> Object getFieldRecursively(@NotNull("clazz") Class<? extends T> clazz, @Nullable T instance, @NotNull("field") String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = findFieldRecursively(clazz, fieldName);
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
    public static <T> Object getFieldWithoutException(@NotNull("clazz") Class<? extends T> clazz, @Nullable T instance, @NotNull("field") String fieldName) {
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
    public static <T> void setField(@NotNull("clazz") Class<? extends T> clazz, @Nullable T instance, @NotNull("field") String fieldName, @Nullable Object value) throws NoSuchFieldException, IllegalAccessException {
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
    public static <T> boolean setFieldWithoutException(@NotNull("clazz") Class<? extends T> clazz, @Nullable T instance, @NotNull("field") String fieldName, @Nullable Object value) {
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
    public static <T> Constructor<? super T> findConstructor(@NotNull("clazz") Class<T> clazz, @Nullable Class<?>... types) {
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
    public static <T> T invokeConstructor(@NotNull("clazz") Class<T> clazz, @NotNull("args") Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
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
    public static <T> T invokeConstructorWithoutException(@NotNull("clazz") Class<? extends T> clazz, @NotNull("args") Object... args) {
        try {
            return invokeConstructor(clazz, args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException ignored) {
            return null;
        }
    }

    @NotNull
    @Contract("_, _ -> new")
    public static List<Class<?>> findAllAnnotatedClasses(@NotNull("packageName") String packageName, @NotNull("annotation class") Class<? extends Annotation> annotation) {
        return new ArrayList<>(new Reflections(packageName).getTypesAnnotatedWith(annotation));
    }

    @NotNull
    public static List<Class<?>> findAllAnnotatedClasses(@Nullable ClassLoader classLoader, @NotNull("packageName") String packageName, @NotNull("annotation class") Class<? extends Annotation> annotation) {
        return new ArrayList<>(new Reflections(
                new ReflectionsConfigurationBuilder()
                        .also(builder -> builder.setClassLoaders(new ClassLoader[] { classLoader, ClassLoader.getSystemClassLoader() }))
                        .setUrls(ClasspathHelper.forClassLoader(classLoader))
                        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName)))
        ).getTypesAnnotatedWith(annotation));
    }

    @NotNull
    public static Set<Class<?>> findAllClasses(@Nullable ClassLoader classLoader, @NotNull("packageName") String packageName, boolean recursive) {
        if (recursive) return findAllClassesRecursive(classLoader, packageName);
        return findAllClasses(classLoader, packageName);
    }

    @NotNull
    public static Set<Class<?>> findAllClasses(@Nullable ClassLoader classLoader, @NotNull("packageName") String packageName) {
        return new Reflections(
                new ReflectionsConfigurationBuilder()
                        .also(builder -> builder.setClassLoaders(new ClassLoader[] { classLoader, ClassLoader.getSystemClassLoader() }))
                        .setUrls(ClasspathHelper.forClassLoader(classLoader))
                        .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName)))
        ).getSubTypesOf(Object.class);
    }

    @SuppressWarnings("UnstableApiUsage")
    @NotNull
    public static Set<Class<?>> findAllClassesRecursive(@Nullable ClassLoader classLoader, @NotNull("packageName") String packageName) {
        try {
            return new ArrayList<>(ClassPath.from(classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader).getTopLevelClassesRecursive(packageName))
                    .stream()
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            Throwables.throwAsUnchecked(e);
            return null;
        }
    }

    @NotNull
    public static List<String> findPackages(@NotNull("prefix") String prefix, boolean recursive) {
        return Arrays.stream(Package.getPackages())
                .map(Package::getName)
                .filter(s -> {
                    if (recursive) return s.toLowerCase().startsWith(prefix.toLowerCase());
                    String c = s.split("\\.")[s.split("\\.").length-1];
                    return s.equalsIgnoreCase(prefix + "." + c);
                })
                .collect(Collectors.toList());
    }

    @NotNull
    public static List<String> findPackages(@Nullable ClassLoader cl, @NotNull("prefix") String prefix, boolean recursive) {
        if (cl == null) return findPackages(prefix, recursive);
        return Arrays.stream(getPackages(cl))
                .map(Package::getName)
                .filter(s -> {
                    if (recursive) return s.toLowerCase().startsWith(prefix.toLowerCase());
                    String c = s.split("\\.")[s.split("\\.").length-1];
                    return s.equalsIgnoreCase(prefix + "." + c);
                })
                .collect(Collectors.toList());
    }

    public static boolean isValidPackage(@NotNull("packageName") String packageName) {
        return Package.getPackage(packageName) != null;
    }

    public static boolean isValidPackage(@Nullable ClassLoader cl, @NotNull("packageName") String packageName) {
        if (cl == null) return isValidPackage(packageName);
        return getPackage(cl, packageName) != null;
    }

    @NotNull
    public static Class<?> getCallerClass() { return getCallerClass(3); } // 2 + this method

    @NotNull
    public static Class<?> getCallerClass(int offset) {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1 + offset; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ReflectionHelper.class.getName()) && !ste.getClassName().contains("java.lang.Thread")) {
                try {
                    return Class.forName(ste.getClassName());
                } catch (ClassNotFoundException ignore) {}
            }
        }
        throw new NoSuchElementException("sorry :(");
    }

    /**
     * Gets all super classes and super interfaces, and return them. The returned entry is not unique and may contains the duplicate entry.
     * @return the super classes and interfaces.
     */
    @NotNull
    public static List<Class<?>> getSupers(@NotNull("clazz") Class<?> clazz) {
        return Lists.concat(getSuperclasses(clazz), getInterfaces(clazz));
    }

    @NotNull
    public static List<Class<?>> getSuperclasses(@NotNull("clazz") Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>();
        Class<?> superclass = clazz;
        while (superclass.getSuperclass() != null) {
            classes.add(superclass.getSuperclass());
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    @NotNull
    public static List<Class<?>> getInterfaces(@NotNull("clazz") Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(clazz);
        for (Class<?> anInterface : clazz.getInterfaces()) classes.addAll(getInterfaces(anInterface));
        Class<?> superclass = clazz;
        while (superclass.getSuperclass() != null) {
            classes.addAll(getInterfaces(superclass.getSuperclass()));
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    /**
     * Creates new instance.
     * @param clazz the class
     * @return new instance of the class
     * @throws RuntimeException if instance could not be created for some reason
     */
    @NotNull
    public static <T> T createInstance(@NotNull Class<T> clazz) throws RuntimeException {
        if (clazz.isInterface()) throw new RuntimeException("Cannot create instance of the interface");
        if (Modifier.isAbstract(clazz.getModifiers())) throw new RuntimeException("Cannot create instance of the abstract class");
        if (clazz.isEnum()) throw new RuntimeException("Cannot create instance of the enum class");
        try {
            return clazz.newInstance();
        } catch (Exception ignore) {}
        return NativeUtil.allocateInstance(clazz);
    }
}
