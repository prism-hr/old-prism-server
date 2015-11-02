package com.zuehlke.pgadmissions.exceptions;

public class IntegrationException extends RuntimeException {

    private static final long serialVersionUID = -1026529671193962765L;

    public IntegrationException(Throwable cause) {
        super(cause);
    }

    public IntegrationException(String message) {
        super(message);
    }

}
