package util.promise.rewrite;

import org.jetbrains.annotations.NotNull;

public class StackTraceAwareThread extends Thread {
    private final Throwable throwable;

    public StackTraceAwareThread(@NotNull Runnable runnable, @NotNull String name, @NotNull Throwable throwable) {
        super(runnable, name);
        this.throwable = throwable;
    }

    @NotNull
    public Throwable getThrowable() {
        return throwable;
    }

    public void printStackTrace() {
        throwable.printStackTrace();
    }
}
