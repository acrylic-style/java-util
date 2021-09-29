package util.promise;

import org.jetbrains.annotations.Nullable;

public class AggregateException extends RuntimeException {
    @Nullable
    protected final Throwable[] errors;

    public AggregateException(String message, @Nullable Throwable[] errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Prints all stack traces, with all errors.
     */
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (this.errors != null)
            for (Throwable error : this.errors) {
                if (error != null) error.printStackTrace();
            }
    }
}
