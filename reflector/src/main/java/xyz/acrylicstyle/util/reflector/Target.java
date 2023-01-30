package xyz.acrylicstyle.util.reflector;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies the target class of the method or class. You must specify either value or clazz.
 * A target constructor, method, or field must be present in the exact target class.
 * The only exception is the default method, which does not require to be annotated.
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface Target {
    @NotNull
    String value() default "";

    @NotNull
    Class<?> clazz() default Object.class;
}
