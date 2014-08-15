package com.zuehlke.pgadmissions.exceptions;

public class WorkflowEngineException extends Exception {

    private static final long serialVersionUID = -116272246657153672L;

    public WorkflowEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowEngineException(String message) {
        super(message);
    }

}
