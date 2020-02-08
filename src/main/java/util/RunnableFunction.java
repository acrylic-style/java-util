package util;

public interface RunnableFunction<T> extends Runnable {
    default void run() {}
    default T runWithType() { return null; }
}
