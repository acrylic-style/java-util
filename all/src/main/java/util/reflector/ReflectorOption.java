package util.reflector;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReflectorOption {
    /**
     * Static prefix to use. For example: if value is <i>static$</i>, then the methods prefixed with <i>static$</i>
     * will call static method on original class.
     * @see Static
     */
    @NotNull
    String staticPrefix() default ";"; // using illegal character effectively prevents from calling static method

    ErrorOption errorOption() default ErrorOption.DEFAULT;

    /**
     * Whether to suppress error message (that is not thrown).
     */
    YesNo suppressMessage() default YesNo.DEFAULT;

    enum ErrorOption {
        THROW_EXCEPTION,
        RETURN_NULL,
        DEFAULT, // => THROW_EXCEPTION
        ;
    }

    enum YesNo {
        YES,
        NO,
        DEFAULT,
        ;
    }
}
