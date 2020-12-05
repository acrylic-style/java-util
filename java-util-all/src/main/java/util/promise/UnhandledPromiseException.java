package util.promise;

public class UnhandledPromiseException extends RuntimeException {
    public UnhandledPromiseException(Throwable cause) {
        super(cause instanceof UnhandledPromiseException && cause.getCause() != null ? cause.getCause() : cause);
    }

    @Override
    public String getMessage() {
        return "Unhandled promise rejection: " + super.getMessage();
    }
}
