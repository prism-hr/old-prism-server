package uk.co.alumeni.prism.exceptions;

public class WorkflowDuplicateResourceException extends RuntimeException {

    private static final long serialVersionUID = -116272246657153672L;

    public WorkflowDuplicateResourceException(String message) {
        super(message);
    }

}
