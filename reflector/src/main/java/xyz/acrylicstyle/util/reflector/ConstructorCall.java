package xyz.acrylicstyle.util.reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks method to call constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConstructorCall {
    /**
     * Target class to call constructor. Object.class is the default value and it will use target class defined at
     * ReflectorHandler. If it was other than Object.class, then the constructor will be called in specified class.
     */
    Class<?> value() default Object.class;
}
