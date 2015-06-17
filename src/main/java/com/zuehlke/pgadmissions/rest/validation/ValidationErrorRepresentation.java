package com.zuehlke.pgadmissions.rest.validation;

public class ValidationErrorRepresentation {

    private String[] fieldNames;

    private String errorMessage;

    public String[] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
