package util;

import org.jetbrains.annotations.Nullable;

public class Validate {
    public static <T> T notNull(@Nullable T t) {
        return notNull(t, "Parameter cannot be null");
    }

    public static <T> T notNull(@Nullable T t, String message) {
        if (t == null) throw new NullPointerException(message);
        return t;
    }
}
