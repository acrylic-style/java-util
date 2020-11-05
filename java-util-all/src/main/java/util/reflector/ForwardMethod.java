package util.reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forwards method call to specified method.
 * If multiple annotations are present, the {@link FieldGetter} or {@link FieldSetter} will be used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ForwardMethod {
    String value();
}
