package uk.co.alumeni.prism.rest.validation;

import java.util.List;

public class ErrorResponseRepresentation {

    private String message;

    private List<ValidationErrorRepresentation> errors;

    public ErrorResponseRepresentation(String message, List<ValidationErrorRepresentation> errors) {
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public List<ValidationErrorRepresentation> getErrors() {
        return errors;
    }
}
