package com.zuehlke.pgadmissions.exceptions;

public class AbortMailSendException extends Exception {

    private static final long serialVersionUID = 1410630386098826284L;

    public AbortMailSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbortMailSendException(String message) {
        super(message);
    }

}
