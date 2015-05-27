package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.Action;

public class WorkflowPermissionException extends RuntimeException {

    private static final long serialVersionUID = -116272246657153672L;

    private Resource fallbackResource;
    
    private Action fallbackAction;

    public WorkflowPermissionException(Resource fallbackResource, Action fallbackAction) {
    	super();
        this.fallbackAction = fallbackAction;
        this.fallbackResource = fallbackResource;
    }

    public final Resource getFallbackResource() {
        return fallbackResource;
    }
    
    public final Action getFallbackAction() {
        return fallbackAction;
    }

}
