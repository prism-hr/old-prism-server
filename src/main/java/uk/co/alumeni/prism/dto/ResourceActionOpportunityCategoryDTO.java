package uk.co.alumeni.prism.dto;

import static com.google.common.base.Objects.equal;
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
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        ResourceActionOpportunityCategoryDTO other = (ResourceActionOpportunityCategoryDTO) object;
        return equal(actionId, other.getActionId()) && super.equals(object);
    }

    @Override
    public String toString() {
        return actionId.getZeroPaddedOrdinal() + getSequenceIdentifier();
    }

}
