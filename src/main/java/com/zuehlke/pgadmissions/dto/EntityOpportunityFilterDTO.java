package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class EntityOpportunityFilterDTO {

    private Integer id;

    private String opportunityCategories;

    private PrismOpportunityType opportunityType;

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

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        EntityOpportunityFilterDTO other = (EntityOpportunityFilterDTO) object;
        return id.equals(other.getId());
    }

}
