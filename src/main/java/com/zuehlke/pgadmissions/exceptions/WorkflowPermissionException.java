package com.zuehlke.pgadmissions.exceptions;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class WorkflowPermissionException extends RuntimeException {

    private static final long serialVersionUID = -116272246657153672L;

    private Resource fallbackResource;

    private PrismAction fallbackAction;

    private String message;

    public WorkflowPermissionException(Resource fallbackResource, PrismAction fallbackAction) {
        this.fallbackAction = fallbackAction;
        this.fallbackResource = fallbackResource;
    }

    public WorkflowPermissionException(Resource fallbackResource, PrismAction fallbackAction, String message) {
        this(fallbackResource, fallbackAction);
        this.message = message;
    }

    public Resource getFallbackResource() {
        return fallbackResource;
    }

    public PrismAction getFallbackAction() {
        return fallbackAction;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
