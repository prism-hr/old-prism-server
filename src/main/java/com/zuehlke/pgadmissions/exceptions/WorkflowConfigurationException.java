package com.zuehlke.pgadmissions.exceptions;

public class WorkflowConfigurationException extends Exception {
    
    private static final long serialVersionUID = 4978904574471246299L;

    public WorkflowConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowConfigurationException(String message) {
        super(message);
    }

}
