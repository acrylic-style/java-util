package util.promise;

public class UncaughtPromiseException extends RuntimeException {
    public UncaughtPromiseException(Throwable throwable) {
        super(throwable);
    }
}
