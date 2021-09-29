package util.base;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class MoreObjects {
    @Contract(value = "null, _ -> param2; !null, _ -> param1", pure = true)
    public static <T> T or(@Nullable T object, @Nullable T another) {
        if (object == null) return another;
        return object;
    }
}
