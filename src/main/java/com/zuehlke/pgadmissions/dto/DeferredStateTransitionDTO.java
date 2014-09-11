package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class DeferredStateTransitionDTO {

    private Class<? extends Resource> resourceClass;

    private Integer resourceId;

    private PrismAction actionId;

    public final Class<? extends Resource> getResourceClass() {
        return resourceClass;
    }

    public DeferredStateTransitionDTO(Class<? extends Resource> resourceClass, Integer resourceId, PrismAction actionId) {
        this.resourceClass = resourceClass;
        this.resourceId = resourceId;
        this.actionId = actionId;
    }

    public final Integer getResourceId() {
        return resourceId;
    }

    public final PrismAction getActionId() {
        return actionId;
    }

}
