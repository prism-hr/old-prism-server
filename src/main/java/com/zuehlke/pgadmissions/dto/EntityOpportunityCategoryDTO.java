package com.zuehlke.pgadmissions.dto;

import com.google.common.base.Objects;

public class EntityOpportunityCategoryDTO {

    private Integer id;

    private String opportunityCategories;

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
        EntityOpportunityCategoryDTO other = (EntityOpportunityCategoryDTO) object;
        return id.equals(other.getId());
    }

}
