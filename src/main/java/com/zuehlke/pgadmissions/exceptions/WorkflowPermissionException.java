package com.zuehlke.pgadmissions.exceptions;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class WorkflowPermissionException extends PrismRequestException {

    private static final long serialVersionUID = -116272246657153672L;

    private PrismAction actionAttempted;

    private PrismAction actionPermitted;

    public WorkflowPermissionException(PrismAction actionAttempted, PrismAction actionPermitted) {
        this.actionAttempted = actionAttempted;
        this.actionPermitted = actionPermitted;
    }
    
    @Override
    public Object getResponseData() {
        return ImmutableMap.of("actionAttempted", actionAttempted, "actionPermitted", actionPermitted);
    }

}
