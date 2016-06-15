package uk.co.alumeni.prism.exceptions;

import org.springframework.validation.Errors;

public class PrismValidationException extends RuntimeException {

    private static final long serialVersionUID = 3960146288462881653L;

    private Errors errors;

    public PrismValidationException(String message) {
        super(message);
    }

    public PrismValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }

}
