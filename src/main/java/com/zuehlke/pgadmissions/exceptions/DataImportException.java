package com.zuehlke.pgadmissions.exceptions;

public class DataImportException extends Exception {

    private static final long serialVersionUID = 649448905824352045L;

    public DataImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataImportException(String message) {
        super(message);
    }

}
