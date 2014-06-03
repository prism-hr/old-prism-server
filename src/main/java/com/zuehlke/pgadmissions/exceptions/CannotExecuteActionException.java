package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;

public class CannotExecuteActionException extends PrismException {

    private static final long serialVersionUID = -1058592315562054622L;
    
    private PrismResource resource;
    
    private PrismAction action;

    public CannotExecuteActionException(PrismResource resource, PrismAction action) {
        this.resource = resource;
        this.action = action;
    }

    public PrismResource getResource() {
        return resource;
    }

    public PrismAction getAction() {
        return action;
    }

}
