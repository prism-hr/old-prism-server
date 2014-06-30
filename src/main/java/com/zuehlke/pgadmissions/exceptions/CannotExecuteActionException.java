package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Resource;

public class CannotExecuteActionException extends PrismException {

    private static final long serialVersionUID = -1058592315562054622L;
    
    private Resource resource;
    
    private Action action;

    public CannotExecuteActionException(Resource resource, Action action) {
        this.resource = resource;
        this.action = action;
    }

    public Resource getResource() {
        return resource;
    }

    public Action getAction() {
        return action;
    }

}
