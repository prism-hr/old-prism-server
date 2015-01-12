package com.zuehlke.pgadmissions.rest.validation;

import java.util.Map;

public class ErrorResponseRepresentation {

    private String message;

    private Map<String, ValidationErrorRepresentation> fieldErrors;

    public ErrorResponseRepresentation(String message, Map<String, ValidationErrorRepresentation> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, ValidationErrorRepresentation> getFieldErrors() {
        return fieldErrors;
    }
}