package util;

public interface ThrowableSupplier<T> {
    T run() throws Throwable;
}
