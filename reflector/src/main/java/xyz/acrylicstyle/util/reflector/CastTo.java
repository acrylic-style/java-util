package xyz.acrylicstyle.util.reflector;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Casts result type to Reflector interface <code>value</code>, it works like {@link Reflector#newReflector(ClassLoader, Class, ReflectorHandler) Reflector#newReflector} And creates instance
 * if needed. (requires createInstance = true) If @CastTo was not found, then ReflectorHandler will try to cast
 * directly. (same as using <code>Type type = (Type) instance;</code>)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CastTo {
    @NotNull
    Class<?> value();

    /**
     * Marks method to create instance of specified class. Target class is required to have constructor with exact
     * 1 parameter count with Object.
     */
    boolean createInstance() default false;
}
