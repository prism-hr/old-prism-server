package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

import javax.validation.constraints.NotNull;

public class ResourceActionDTO {

    private PrismAction actionId;

    private Integer resourceId;

    public ResourceActionDTO(PrismAction actionId, Integer resourceId) {
        this.actionId = actionId;
        this.resourceId = resourceId;
    }

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }
}
