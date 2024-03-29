package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;

import java.math.BigDecimal;

import static com.google.common.base.Objects.equal;
import static org.apache.commons.lang.StringUtils.rightPad;
import static org.apache.commons.lang3.ObjectUtils.compare;
import static uk.co.alumeni.prism.PrismConstants.*;

public class EntityOpportunityCategoryDTO implements Comparable<EntityOpportunityCategoryDTO> {

    private Integer id;

    private String opportunityCategories;

    private PrismOpportunityType opportunityType;

    private BigDecimal priority;

    private String sequenceIdentifier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpportunityCategories() {
        return opportunityCategories;
    }

    public void setOpportunityCategories(String opportunityCategories) {
        this.opportunityCategories = opportunityCategories;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public BigDecimal getPriority() {
        return priority;
    }

    public void setPriority(BigDecimal priority) {
        this.priority = priority;
    }

    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        EntityOpportunityCategoryDTO other = (EntityOpportunityCategoryDTO) object;
        return equal(id, other.getId());
    }

    @Override
    public String toString() {
        String prefix = (priority == null ? new BigDecimal(0) : priority).toPlainString().replace(FULL_STOP, "");
        return rightPad(prefix, (ORDERING_PRECISION + 1), ZERO) + sequenceIdentifier;
    }

    @Override
    public int compareTo(EntityOpportunityCategoryDTO other) {
        return compare(other.toString(), toString());
    }

}
