package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.rest.representation.resource.ActionRepresentation;

public class ResourceListActionDTO {

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

    public ActionRepresentation getActionRepresentation() {
        return new ActionRepresentation().withId(actionId).withRaisesUrgentFlag(raisesUrgentFlag).withPrimaryState(primaryState);
    }

}
