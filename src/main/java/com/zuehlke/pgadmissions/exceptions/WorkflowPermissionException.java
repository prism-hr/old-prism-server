package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.Action;

public class WorkflowPermissionException extends RuntimeException {

    private static final long serialVersionUID = -116272246657153672L;

    private Action action;
    
    private Action fallbackAction;
    
    private Resource resource;
    
    private Resource fallbackResource;

    private String message;

    public WorkflowPermissionException(Action action,  Action fallbackAction, Resource resource, Resource fallbackResource) {
        this.action = action;
        this.fallbackAction = fallbackAction;
        this.fallbackResource = fallbackResource;
    }

    public WorkflowPermissionException(Action action, Action fallbackAction, Resource resource, Resource fallbackResource, String message) {
        this(action, fallbackAction, resource, fallbackResource);
        this.message = message;
    }

    public final Action getAction() {
        return action;
    }

    public final Action getFallbackAction() {
        return fallbackAction;
    }
    
    public final Resource getResource() {
        return resource;
    }

    public final Resource getFallbackResource() {
        return fallbackResource;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
}
