package util.promise;

public class UnhandledPromiseException extends RuntimeException {
    public UnhandledPromiseException(Throwable throwable) {
        super(throwable);
    }

    @Override
    public String getMessage() {
        return "Unhandled promise rejection: " + super.getMessage();
    }
}
