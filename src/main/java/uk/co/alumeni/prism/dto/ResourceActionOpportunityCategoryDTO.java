package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;

import com.google.common.base.Objects;

public class ResourceActionOpportunityCategoryDTO extends ResourceOpportunityCategoryDTO {

    private PrismAction actionId;

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), actionId);
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object) && actionId.equals(((ResourceActionOpportunityCategoryDTO) object).getActionId());
    }

    @Override
    public String toString() {
        return actionId.getZeroPaddedOrdinal() + getSequenceIdentifier();
    }

}
