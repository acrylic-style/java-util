package util.base;

import org.jetbrains.annotations.NotNull;
import util.ActionableResult;
import util.ThrowableActionableResult;
import util.Validate;

import java.lang.reflect.Field;

public final class Enums {
    @NotNull
    public static <E extends Enum<E>> ActionableResult<E> valueOf(@NotNull Class<E> clazz, @NotNull String s) {
        Validate.notNull(clazz, "class cannot be null");
        Validate.notNull(s, "string cannot be null");
        return ThrowableActionableResult.of(() -> Enum.valueOf(clazz, s));
    }

    @NotNull
    public static ActionableResult<Field> getField(@NotNull Enum<?> enumKey) {
        return ThrowableActionableResult.of(() -> enumKey.getDeclaringClass().getDeclaredField(enumKey.name()));
    }
}
