package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SneakyThrow {
    @SuppressWarnings({ "ConstantConditions", "Contract" })
    @Contract("_ -> fail") // its result value should not be used and should be ignored
    @NotNull
    public static <T> T sneaky(@Nullable Throwable exception) {
        if (exception == null) return null;
        SneakyThrow.throwSneaky(exception);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwSneaky(@NotNull Throwable exception) throws T {
        throw (T) exception;
    }
}
