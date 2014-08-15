package com.zuehlke.pgadmissions.exceptions;

import org.springframework.validation.Errors;

public class PrismValidationException extends RuntimeException {

    private Errors errors;

    public PrismValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
