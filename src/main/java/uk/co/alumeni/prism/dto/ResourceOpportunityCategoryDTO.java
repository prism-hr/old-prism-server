package uk.co.alumeni.prism.dto;

import org.joda.time.DateTime;

public class ResourceOpportunityCategoryDTO extends EntityOpportunityCategoryDTO<ResourceOpportunityCategoryDTO> {

    private Boolean raisesUrgentFlag;

    private Boolean raisesMessageFlag;

    private DateTime updatedTimestamp;

    private Boolean onlyAsPartner;

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Boolean getRaisesMessageFlag() {
        return raisesMessageFlag;
    }

    public void setRaisesMessageFlag(Boolean raisesMessageFlag) {
        this.raisesMessageFlag = raisesMessageFlag;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public Boolean getOnlyAsPartner() {
        return onlyAsPartner;
    }

    public void setOnlyAsPartner(Boolean onlyAsPartner) {
        this.onlyAsPartner = onlyAsPartner;
    }

}
