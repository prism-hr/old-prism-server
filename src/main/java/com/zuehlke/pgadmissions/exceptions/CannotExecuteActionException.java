package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.PrismResource;

public class CannotExecuteActionException extends PrismException {

    private static final long serialVersionUID = -1058592315562054622L;
    
    private PrismResource resource;
    
    private Action action;

    public CannotExecuteActionException(PrismResource resource, Action action) {
        this.resource = resource;
        this.action = action;
    }

    public PrismResource getResource() {
        return resource;
    }

    public Action getAction() {
        return action;
    }

}
