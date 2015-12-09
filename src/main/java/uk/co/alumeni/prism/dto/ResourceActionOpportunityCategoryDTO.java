package uk.co.alumeni.prism.dto;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;

import com.google.common.base.Objects;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;

public class ResourceActionOpportunityCategoryDTO extends EntityOpportunityCategoryDTO<ResourceActionOpportunityCategoryDTO> {

    private PrismAction actionId;

    private DateTime updatedTimestamp;

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
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

    @Override
    public int compareTo(ResourceActionOpportunityCategoryDTO other) {
        return ObjectUtils.compare(toString(), other.toString());
    }

}
