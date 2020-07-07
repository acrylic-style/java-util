package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class Validate {
    @Contract("null -> fail; !null -> param1")
    public static <T> T notNull(@Nullable T t) {
        return notNull(t, "Parameter cannot be null");
    }

    @Contract("null, _ -> fail; !null, _ -> param1")
    public static <T> T notNull(@Nullable T t, String message) {
        if (t == null) throw new NullPointerException(message);
        return t;
    }
}
