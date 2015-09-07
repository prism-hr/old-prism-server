package com.zuehlke.pgadmissions.rest.dto.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

public class ResourceConditionDTO {

    private PrismActionCondition actionCondition;

    private Boolean partnerMode;

    public PrismActionCondition getActionCondition() {
        return actionCondition;
    }

    public void setActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
    }

    public Boolean getPartnerMode() {
        return partnerMode;
    }

    public void setPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
    }
    
    public ResourceConditionDTO withActionCondition(PrismActionCondition actionCondition) {
        this.actionCondition = actionCondition;
        return this;
    }

    public ResourceConditionDTO withPartnerMode() {
        this.partnerMode = true;
        return this;
    }

}
