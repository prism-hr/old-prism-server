package com.zuehlke.pgadmissions.exceptions;

public class ApplicationExportException extends Exception {

    private static final long serialVersionUID = 1410630386098826284L;

    public ApplicationExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationExportException(String message) {
        super(message);
    }

}
