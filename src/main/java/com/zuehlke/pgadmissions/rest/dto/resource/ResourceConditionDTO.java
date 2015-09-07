package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

public class ResourceConditionDTO {

    private PrismActionCondition actionCondition;

    private Boolean internalMode;
    
    private Boolean externalMode;

    public PrismActionCondition getActionCondition() {
        return actionCondition;
    }

    public void setActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
    }
    
    public Boolean getInternalMode() {
        return internalMode;
    }

    public void setInternalMode(Boolean internalMode) {
        this.internalMode = internalMode;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public void setExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
    }

    public ResourceConditionDTO withActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
        return this;
    }

    public ResourceConditionDTO withInternalMode(Boolean internalMode) {
        this.internalMode = internalMode;
        return this;
    }

    public ResourceConditionDTO withExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
        return this;
    }
    
}
