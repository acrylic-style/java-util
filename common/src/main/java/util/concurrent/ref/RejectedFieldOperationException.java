package util.concurrent.ref;

public class RejectedFieldOperationException extends RuntimeException {
    public RejectedFieldOperationException() {
        super();
    }

    public RejectedFieldOperationException(Throwable throwable) {
        super(throwable);
    }

    public RejectedFieldOperationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RejectedFieldOperationException(String message) {
        super(message);
    }
}
