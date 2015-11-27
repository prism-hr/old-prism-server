package uk.co.alumeni.prism.dto;

public class ResourceOpportunityCategoryDTO extends EntityOpportunityFilterDTO {

    private Boolean raisesUrgentFlag;

    private Boolean onlyAsPartner;

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Boolean getOnlyAsPartner() {
        return onlyAsPartner;
    }

    public void setOnlyAsPartner(Boolean onlyAsPartner) {
        this.onlyAsPartner = onlyAsPartner;
    }

}
