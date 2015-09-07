package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition;

public class AdvertActionConditionDTO {

    private Integer advertId;

    private PrismActionCondition actionCondition;

    private Boolean internalMode;

    private Boolean externalMode;

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

}
