package util.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ReflectionHelper;
import util.SneakyThrow;

import java.lang.annotation.Annotation;

public class RefClass<T> {
    @NotNull
    private final Class<T> clazz;

    public RefClass(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <C> RefClass<C> forName(@NotNull String clazz) {
        try {
            return new RefClass<>((Class<C>) Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            return SneakyThrow.sneaky(e);
        }
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <C> RefClass<C> forName(@NotNull String clazz, boolean initialize, @Nullable ClassLoader classLoader) {
        try {
            return new RefClass<>((Class<C>) Class.forName(clazz, initialize, classLoader));
        } catch (ClassNotFoundException e) {
            return SneakyThrow.sneaky(e);
        }
    }

    @Contract(pure = true)
    @NotNull
    public RefClass<?> unchecked() { return Ref.getClassUnchecked(clazz); }

    @NotNull
    public Class<T> getClazz() { return clazz; }

    @Contract(pure = true)
    @NotNull
    public RefField<T> getDeclaredField(String fieldName) { return Ref.getDeclaredField(this.clazz, fieldName); }

    @Contract(pure = true)
    @NotNull
    public RefField<T> getField(String fieldName) { return Ref.getField(this.clazz, fieldName); }

    @Contract(pure = true)
    @NotNull
    public RefConstructor<T> getConstructor(Class<?>... classes) { return Ref.getConstructor(this.clazz, classes); }

    @Contract(pure = true)
    @NotNull
    public RefConstructor<T> getDeclaredConstructor(Class<?>... classes) { return Ref.getDeclaredConstructor(this.clazz, classes); }

    @Contract(pure = true)
    @NotNull
    public RefConstructor<T>[] getConstructors() { return Ref.getConstructors(this.clazz); }

    @Contract(pure = true)
    @NotNull
    public RefConstructor<T>[] getDeclaredConstructors() { return Ref.getDeclaredConstructors(this.clazz); }

    @Contract(pure = true)
    @NotNull
    public RefMethod<T> getMethod(String methodName, Class<?>... classes) { return Ref.getMethod(this.clazz, methodName, classes); }

    @Contract(pure = true)
    @NotNull
    public RefMethod<T> getDeclaredMethod(String methodName, Class<?>... classes) { return Ref.getDeclaredMethod(this.clazz, methodName, classes); }

    @Contract(pure = true)
    @NotNull
    public RefMethod<T>[] getMethods() { return Ref.getMethods(this.clazz); }

    @Contract(pure = true)
    @NotNull
    public RefMethod<T>[] getDeclaredMethods() { return Ref.getDeclaredMethods(this.clazz); }

    @Contract(pure = true)
    @NotNull
    public RefField<T>[] getFields() { return Ref.getFields(this.clazz); }

    @Contract(pure = true)
    @NotNull
    public RefField<T>[] getDeclaredFields() { return Ref.getDeclaredFields(this.clazz); }

    @Contract(pure = true)
    @NotNull
    public <U> RefClass<? extends U> asSubClass(Class<U> clazz) { return new RefClass<>(this.clazz.asSubclass(clazz)); }

    @Contract(pure = true)
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) { return this.clazz.getAnnotation(annotationClass); }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) { return this.clazz.isAnnotationPresent(annotationClass); }

    @Contract(pure = true)
    @NotNull
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) { return this.clazz.getAnnotationsByType(annotationClass); }

    @NotNull
    public Annotation[] getAnnotations() { return this.clazz.getAnnotations(); }

    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) { return this.clazz.getDeclaredAnnotation(annotationClass); }

    @NotNull
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationClass) { return this.clazz.getDeclaredAnnotationsByType(annotationClass); }

    @NotNull
    public Annotation[] getDeclaredAnnotations() { return this.clazz.getDeclaredAnnotations(); }

    @Contract(value = "_ -> param1", pure = true)
    public T cast(Object obj) { return this.clazz.cast(obj); }

    public T[] getEnumConstants() { return this.clazz.getEnumConstants(); }

    public boolean isExtends(Class<?> clazz) {
        return ReflectionHelper.getSupers(this.clazz).contains(clazz);
    }
}
