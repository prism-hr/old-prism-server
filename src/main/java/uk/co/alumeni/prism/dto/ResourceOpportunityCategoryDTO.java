package uk.co.alumeni.prism.dto;

import org.joda.time.DateTime;

public class ResourceOpportunityCategoryDTO extends EntityOpportunityCategoryDTO<ResourceOpportunityCategoryDTO> {

    private Boolean raisesUrgentFlag;

    private Integer readMessageCount;

    private Integer unreadMessageCount;

    private Integer creatorMessageCount;

    private DateTime updatedTimestamp;

    private Boolean onlyAsPartner;

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public Integer getReadMessageCount() {
        return readMessageCount;
    }

    public void setReadMessageCount(Integer readMessageCount) {
        this.readMessageCount = readMessageCount;
    }

    public Integer getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(Integer unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public Integer getCreatorMessageCount() {
        return creatorMessageCount;
    }

    public void setCreatorMessageCount(Integer creatorMessageCount) {
        this.creatorMessageCount = creatorMessageCount;
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
