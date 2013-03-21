package com.zuehlke.pgadmissions.exceptions;

public abstract class PgadmissionsException extends RuntimeException {

    private static final long serialVersionUID = 1520657238240701657L;

    public PgadmissionsException() {
        super();
    }

    public PgadmissionsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PgadmissionsException(String message) {
        super(message);
    }

    public PgadmissionsException(Throwable cause) {
        super(cause);
    }

}
