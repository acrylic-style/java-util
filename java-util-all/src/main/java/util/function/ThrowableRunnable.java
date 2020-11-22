package util.function;

@FunctionalInterface
public interface ThrowableRunnable extends Runnable {
    /**
     * {@inheritDoc}
     * This method ignores any exception. Use this only when "impossible exceptions".
     */
    @Override
    default void run() {
        try {
            doTask();
        } catch (Throwable ignore) {}
    }

    void doTask() throws Throwable;
}
