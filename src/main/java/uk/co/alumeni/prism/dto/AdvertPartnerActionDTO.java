package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;

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
