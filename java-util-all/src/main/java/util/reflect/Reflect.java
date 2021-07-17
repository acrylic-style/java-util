package util.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Reflect {
    @Contract(pure = true)
    public static <T> @NotNull WrappedObject<T> on(@NotNull T value) {
        Objects.requireNonNull(value);
        return new WrappedObject<>(value);
    }
}
