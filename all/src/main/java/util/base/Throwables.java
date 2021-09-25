package util.base;

import org.jetbrains.annotations.NotNull;

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
}
