package com.zuehlke.pgadmissions.exceptions;

public class PrismException extends RuntimeException {

    private static final long serialVersionUID = 1520657238240701657L;

    public PrismException() {
        super();
    }

    public PrismException(String message, Throwable cause) {
        super(message, cause);
    }

    public PrismException(String message) {
        super(message);
    }

    public PrismException(Throwable cause) {
        super(cause);
    }

}