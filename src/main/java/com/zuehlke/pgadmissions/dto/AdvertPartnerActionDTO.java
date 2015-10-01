package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

public class AdvertPartnerActionDTO {

    private Integer advertId;

    private PrismActionCondition actionCondition;

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

}
