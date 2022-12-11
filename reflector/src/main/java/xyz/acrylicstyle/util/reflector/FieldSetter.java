package xyz.acrylicstyle.util.reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that marks method to set field, even if there is method with same name.
 * Requires exactly one argument on method to work.
 * If multiple annotations are present, the {@link FieldGetter} will be used.
 * Please note that this annotation is not required when it's possible to auto-detect.
 * Auto-detect requires method name to starts with "set", matches the field name and there must not be conflicting method name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FieldSetter {
    /**
     * The field name to get. If left empty, it will find automatically.
     */
    String value() default "";

    boolean removeFinal() default true;
}
