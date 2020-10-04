package util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

public class Validate {
    public static <T> T notNull(@Nullable T t) {
        return notNull(t, "Parameter cannot be null");
    }

    public static <T> T notNull(@Nullable T t, String message) {
        if (t == null) throw new NullPointerException(message);
        return t;
    }

    public static void isTrue(boolean condition) { isTrue(condition, "Condition must be true"); }

    public static void isTrue(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
