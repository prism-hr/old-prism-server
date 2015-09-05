package com.zuehlke.pgadmissions.dto.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class ResourceStateDTO {

    private Integer resourceId;

    private PrismState stateId;

    private Boolean primaryState;

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public PrismState getStateId() {
        return stateId;
    }

    public void setStateId(PrismState stateId) {
        this.stateId = stateId;
    }

    public Boolean getPrimaryState() {
        return primaryState;
    }

    public void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

}
