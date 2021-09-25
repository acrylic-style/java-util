package util.reflect;

import org.jetbrains.annotations.NotNull;

public class ReflectException extends RuntimeException {
    ReflectException(@NotNull String message) {
        super(message);
    }

    ReflectException(@NotNull Throwable cause) {
        super(cause);
    }
}
