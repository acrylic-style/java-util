package xyz.acrylicstyle.util.reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks parameter to transform proxied instance (ReflectorHandler) into object form. Without it, ReflectorHandler will
 * try to invoke method with ReflectorHandler.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransformParam {
}
