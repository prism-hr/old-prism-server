package com.zuehlke.pgadmissions.exceptions;

public class WorkflowDuplicateResourceException extends RuntimeException {

    private static final long serialVersionUID = -116272246657153672L;

    public WorkflowDuplicateResourceException(String message) {
        super(message);
    }

}
