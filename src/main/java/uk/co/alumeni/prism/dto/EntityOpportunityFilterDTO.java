package uk.co.alumeni.prism.dto;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Objects;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;

public class EntityOpportunityFilterDTO implements Comparable<EntityOpportunityFilterDTO> {

    private Integer id;

    private String opportunityCategories;

    private PrismOpportunityType opportunityType;

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
        EntityOpportunityFilterDTO other = (EntityOpportunityFilterDTO) object;
        return id.equals(other.getId());
    }

    @Override
    public int compareTo(EntityOpportunityFilterDTO other) {
        return ObjectUtils.compare(other.getSequenceIdentifier(), sequenceIdentifier);
    }

}
