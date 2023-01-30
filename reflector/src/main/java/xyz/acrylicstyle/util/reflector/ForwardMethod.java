package xyz.acrylicstyle.util.reflector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Forwards method call to specified method.
 * If multiple annotations are present, the {@link FieldGetter} or {@link FieldSetter} will be used.
 * When used on parameter, it will invoke method then pass returned value to the original method call.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface ForwardMethod {
    /**
     * The method to call. This can be a method name or a method signature (like <code>method(Ljava/lang/Object;)V</code>).
     * @return method name or signature
     */
    String value();
    Class<?> target() default Object.class;
}
