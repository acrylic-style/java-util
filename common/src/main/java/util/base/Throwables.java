package util.base;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import util.Validate;

import java.io.PrintStream;

public class Throwables {
    public static void printStackTrace(@NotNull StackTraceElement[] elements) {
        printStackTrace(elements, System.err);
    }

    public static void printStackTrace(@NotNull StackTraceElement[] elements, @NotNull PrintStream p) {
        for (StackTraceElement element : elements) {
            p.println("\tat " + element);
        }
    }

    @Contract("_ -> fail")
    public static void throwAsUnchecked(@NotNull Throwable throwable) {
        Validate.notNull(throwable, "throwable cannot be null");
        if (throwable instanceof RuntimeException) throw (RuntimeException) throwable;
        throw new RuntimeException(throwable);
    }
}
