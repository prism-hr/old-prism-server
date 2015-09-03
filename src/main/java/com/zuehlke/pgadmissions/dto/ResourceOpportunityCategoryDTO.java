package com.zuehlke.pgadmissions.dto;

public class ResourceOpportunityCategoryDTO extends EntityOpportunityCategoryDTO {

    private Boolean raisesUrgentFlag;

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

}
