package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class SneakyThrow {
    @SuppressWarnings("SameReturnValue")
    @Contract(value = "_ -> fail")
    @NotNull
    public static <T> T sneaky(@NotNull Throwable exception) {
        SneakyThrow.throwSneaky(exception);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwSneaky(@NotNull Throwable exception) throws T {
        throw (T) exception;
    }
}
