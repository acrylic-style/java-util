package util;

import util.function.ThrowableSupplier;

/**
 * Defines static method/interface that will throw exception when
 * the method thrown the any exception or errors.
 * In short, it can be used to throw exceptions that occurs in supplier, but without declaring the "throws".
 */
public class Thrower {
    public static <T> T invoke(ThrowableSupplier<T> t) {
        try {
            return t.get();
        } catch (Throwable throwable) {
            SneakyThrow.sneaky(throwable);
            return null; // unreachable
        }
    }
}
