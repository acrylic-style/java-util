package util.reflector;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Casts result type to Reflector interface [value]. And creates instance if needed. (requires createInstance = true)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CastTo {
    @NotNull
    Class<?> value();

    boolean createInstance() default false;
}
