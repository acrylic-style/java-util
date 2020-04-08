package util;

/**
 * Defines static method/interface that will throw RuntimeException when
 * the method thrown the any exception.
 */
public class RuntimeExceptionThrower {
    public static <T> T invoke(ThrowableConsumer<T> t) {
        try {
            return t.run();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public interface ThrowableConsumer<T> {
        T run() throws Throwable;
    }
}
