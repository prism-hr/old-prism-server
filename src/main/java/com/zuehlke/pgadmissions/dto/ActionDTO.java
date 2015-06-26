package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class ActionDTO {

    private Integer resourceId;

    private PrismAction actionId;

    private Boolean raisesUrgentFlag;

    private Boolean primaryState;

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Boolean getPrimaryState() {
        return primaryState;
    }

    public void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(resourceId, actionId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ActionDTO other = (ActionDTO) object;
        return Objects.equal(resourceId, other.getResourceId()) && Objects.equal(actionId, other.getActionId());
    }

}
