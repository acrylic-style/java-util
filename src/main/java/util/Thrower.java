package util;

/**
 * Defines static method/interface that will throw exception when
 * the method thrown the any exception or errors.
 */
public class Thrower {
    public static <T> T invoke(ThrowableConsumer<T> t) {
        try {
            return t.run();
        } catch (Throwable throwable) {
            SneakyThrow.sneaky(throwable);
            return null; // unreachable
        }
    }

    public interface ThrowableConsumer<T> {
        T run() throws Throwable;
    }
}
