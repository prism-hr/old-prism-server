package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class AdvertPartnerActionDTO {

    private Integer advertId;

    private PrismAction actionId;

    public Integer getAdvertId() {
        return advertId;
    }

    public void setAdvertId(Integer advertId) {
        this.advertId = advertId;
    }

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionCondition) {
        this.actionId = actionCondition;
    }

}
