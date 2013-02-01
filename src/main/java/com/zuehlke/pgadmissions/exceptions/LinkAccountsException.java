package com.zuehlke.pgadmissions.exceptions;

public class LinkAccountsException extends Exception {

    private static final long serialVersionUID = 6512176823218226899L;

    public LinkAccountsException() {
    }

    public LinkAccountsException(String message) {
        super(message);
    }

    public LinkAccountsException(Throwable cause) {
        super(cause);
    }

    public LinkAccountsException(String message, Throwable cause) {
        super(message, cause);
    }

}
