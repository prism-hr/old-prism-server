package com.zuehlke.pgadmissions.rest.validation;

import org.springframework.validation.Errors;

public class InvalidRequestException extends RuntimeException {

    private static final long serialVersionUID = 50649966285340632L;
    
    private Errors errors;

    public InvalidRequestException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public Errors getErrors() { return errors; }
}