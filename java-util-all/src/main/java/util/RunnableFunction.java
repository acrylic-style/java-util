package util;

/**
 * Defines function that may be returns type.
 */
public interface RunnableFunction<T> extends Runnable {
    /**
     * Run without any return type.
     */
    default void run() {}

    /**
     * Run with return type.
     */
    default T runWithType() { return null; }
}
