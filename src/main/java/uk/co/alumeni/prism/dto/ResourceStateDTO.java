package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;

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
