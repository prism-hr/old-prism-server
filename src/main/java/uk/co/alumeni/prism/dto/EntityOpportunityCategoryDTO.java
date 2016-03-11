package uk.co.alumeni.prism.dto;

import static org.apache.commons.lang3.ObjectUtils.compare;

import org.apache.commons.lang3.BooleanUtils;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;

import com.google.common.base.Objects;

public class EntityOpportunityCategoryDTO<T extends EntityOpportunityCategoryDTO<?>> implements Comparable<T> {

    private Integer id;

    private String opportunityCategories;

    private PrismOpportunityType opportunityType;

    private Boolean prioritize;

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

    public Boolean getPrioritize() {
        return prioritize;
    }

    public void setPrioritize(Boolean prioritize) {
        this.prioritize = prioritize;
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
    @SuppressWarnings("unchecked")
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!getClass().equals(object.getClass())) {
            return false;
        }
        T other = (T) object;
        return id.equals(other.getId());
    }

    @Override
    public String toString() {
        return (BooleanUtils.toBoolean(prioritize) ? 1 : 0) + sequenceIdentifier;
    }

    @Override
    public int compareTo(T other) {
        return compare(other.toString(), toString());
    }

}
