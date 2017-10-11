package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;

public class ActionDTO implements Comparable<ActionDTO> {

    private Integer resourceId;

    private PrismAction actionId;

    private Boolean raisesUrgentFlag;

    private Boolean onlyAsPartner;

    private Boolean primaryState;

    private Boolean declinable;

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

    public Boolean getOnlyAsPartner() {
        return onlyAsPartner;
    }

    public void setOnlyAsPartner(Boolean onlyAsPartner) {
        this.onlyAsPartner = onlyAsPartner;
    }

    public Boolean getPrimaryState() {
        return primaryState;
    }

    public void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public Boolean getDeclinable() {
        return declinable;
    }

    public void setDeclinable(Boolean declinable) {
        this.declinable = declinable;
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
        final ActionDTO other = (ActionDTO) object;
        return Objects.equal(resourceId, other.getResourceId()) && Objects.equal(actionId, other.getActionId());
    }

    @Override
    public int compareTo(ActionDTO other) {
        int resourceComparison = resourceId.compareTo(other.getResourceId());
        if (resourceComparison == 0) {
            int urgentComparison = other.getRaisesUrgentFlag().compareTo(raisesUrgentFlag);
            return urgentComparison == 0 ? actionId.name().compareTo(other.getActionId().name()) : urgentComparison;
        }
        return resourceComparison;
    }

}
