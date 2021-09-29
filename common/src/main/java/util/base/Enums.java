package util.base;

import org.jetbrains.annotations.NotNull;
import util.ActionableResult;
import util.ThrowableActionableResult;
import util.Validate;

import java.lang.reflect.Field;

public class Enums {
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

    @NotNull
    public static String getFriendlyName(@NotNull Enum<?> anEnum) {
        Validate.notNull(anEnum, "anEnum cannot be null");
        String name = anEnum.name().replaceAll("_", " ").toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
