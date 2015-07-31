package com.zuehlke.pgadmissions.exceptions;

public class ScrapingException extends RuntimeException {

    private static final long serialVersionUID = -1026529671193962765L;

    public ScrapingException(Throwable cause) {
        super(cause);
    }

    public ScrapingException(String message) {
        super(message);
    }

}
