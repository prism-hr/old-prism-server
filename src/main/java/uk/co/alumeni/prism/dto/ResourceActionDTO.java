package uk.co.alumeni.prism.dto;

import org.joda.time.DateTime;

import com.google.common.base.Objects;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;

public class ResourceActionDTO {

    private Integer resourceId;

    private PrismAction actionId;

    private Boolean raisesUrgentFlag;

    private DateTime updatedTimestamp;

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public Boolean getRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }

    public void setRaisesUrgentFlag(Boolean raisesUrgentFlag) {
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(resourceId, actionId);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ResourceActionDTO other = (ResourceActionDTO) object;
        return Objects.equal(resourceId, other.getResourceId()) && Objects.equal(actionId, other.getActionId());
    }

}
