package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UUIDUtil {
    public static final UUID NULL = new UUID(0L, 0L);

    @NotNull
    public static UUID uuidFromStringWithoutDashes(@Nullable String s) {
        if (s == null) return NULL;
        return UUID.fromString(s.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }
}
