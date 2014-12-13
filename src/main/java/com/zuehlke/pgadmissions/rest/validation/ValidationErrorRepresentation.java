package com.zuehlke.pgadmissions.rest.validation;

public class ValidationErrorRepresentation {

    private String code;

    private String message;

    private Object[] arguments;

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}