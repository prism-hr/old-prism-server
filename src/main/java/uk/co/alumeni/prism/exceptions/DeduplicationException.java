package uk.co.alumeni.prism.exceptions;

public class DeduplicationException extends RuntimeException {

    private static final long serialVersionUID = -240548162971064137L;

    public DeduplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeduplicationException(String message) {
        super(message);
    }

}
