package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.workflow.Action;

public class WorkflowPermissionException extends RuntimeException {

    private static final long serialVersionUID = -116272246657153672L;

    private Action fallbackAction;

    private Resource fallbackResource;

    public WorkflowPermissionException(String message, Action fallbackAction, Resource fallbackResource) {
        super(message);
        this.fallbackAction = fallbackAction;
        this.fallbackResource = fallbackResource;
    }

    public final Action getFallbackAction() {
        return fallbackAction;
    }

    public final Resource getFallbackResource() {
        return fallbackResource;
    }

}
