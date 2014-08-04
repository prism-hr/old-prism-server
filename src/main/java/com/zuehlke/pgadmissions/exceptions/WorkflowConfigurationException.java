package com.zuehlke.pgadmissions.exceptions;

public class WorkflowConfigurationException extends Exception {

    private static final String WORKFLOW_CONFIGURATION_FAILURE = "The workflow configuration change that you tried to make is invalid.";
    
    private static final long serialVersionUID = 4978904574471246299L;
    
    public WorkflowConfigurationException() {
        super(WORKFLOW_CONFIGURATION_FAILURE);
    }

    public WorkflowConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowConfigurationException(String message) {
        super(message);
    }

    public WorkflowConfigurationException(Throwable cause) {
        super(WORKFLOW_CONFIGURATION_FAILURE, cause);
    }

}
