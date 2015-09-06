package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

public class AdvertActionConditionDTO {

    private Integer advertId;

    private PrismActionCondition actionCondition;

    private Boolean partnerMode;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

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

}
