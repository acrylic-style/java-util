package util;

import org.jetbrains.annotations.NotNull;

public class EnumUtil {
    @NotNull
    public static String getFriendlyName(@NotNull Enum<?> anEnum) {
        Validate.notNull(anEnum, "anEnum cannot be null");
        String name = anEnum.name().replaceAll("_", " ").toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
