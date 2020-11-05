package util.reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that marks method to get field, even if there is method with same name.
 * Requires exactly zero argument on method to work.
 * If multiple annotations are present, the {@link FieldGetter} will be used.
 * Please note that this annotation is not required when it's possible to auto-detect.
 * Auto-detect requires method name to starts with "get", matches the field name and there must not be conflicting method name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FieldGetter {
    /**
     * The field name to get. If left empty, it will find automatically.
     */
    String value() default "";
}
