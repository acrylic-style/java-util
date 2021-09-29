package util.function;

/**
 * Defines function that may be returns type.
 */
public interface RunnableSupplier<T> extends Runnable {
    /**
     * Run without any return type.
     */
    default void run() {}

    /**
     * Run with return type.
     */
    default T runWithType() { return null; }
}
