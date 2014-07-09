package com.zuehlke.pgadmissions.exceptions;

public class StateTransitionException extends Exception {

    private static final long serialVersionUID = -116272246657153672L;
    
    public StateTransitionException() {
        super();
    }

    public StateTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateTransitionException(String message) {
        super(message);
    }

    public StateTransitionException(Throwable cause) {
        super(cause);
    }

}
