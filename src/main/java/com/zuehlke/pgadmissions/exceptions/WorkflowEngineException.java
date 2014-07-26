package com.zuehlke.pgadmissions.exceptions;

public class WorkflowEngineException extends Exception {
    
    private static final String WORKFLOW_ENGINE_FAILURE = "The action that you tried to perform was invalid.";

    private static final long serialVersionUID = -116272246657153672L;
    
    public WorkflowEngineException() {
        super(WORKFLOW_ENGINE_FAILURE);
    }

    public WorkflowEngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowEngineException(String message) {
        super(message);
    }

    public WorkflowEngineException(Throwable cause) {
        super(WORKFLOW_ENGINE_FAILURE, cause);
    }

}
