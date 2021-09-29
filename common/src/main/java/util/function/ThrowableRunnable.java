package util.function;

@FunctionalInterface
public interface ThrowableRunnable extends Runnable {
    /**
     * {@inheritDoc}
     * This method ignores any exception. Use this only when "impossible exceptions".
     *
     * @see #doTask()
     */
    @Override
    default void run() {
        try {
            doTask();
        } catch (Throwable ignore) {}
    }

    /**
     * Executes the callback.
     *
     * @throws Throwable throwable
     * @see #run()
     */
    void doTask() throws Throwable;
}
