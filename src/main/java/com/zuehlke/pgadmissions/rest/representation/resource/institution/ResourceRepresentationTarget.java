package com.zuehlke.pgadmissions.rest.representation.resource.institution;

import java.math.BigDecimal;
import java.util.List;

import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationLocation;

public class ResourceRepresentationTarget extends ResourceRepresentationLocation {

    private BigDecimal relevance;

    private BigDecimal distance;

    private Boolean selected;

    private BigDecimal rating;

    private List<ResourceRepresentationLocation> departments;

    public BigDecimal getRelevance() {
        return relevance;
    }

    public void setRelevance(BigDecimal relevance) {
        this.relevance = relevance;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public List<ResourceRepresentationLocation> getDepartments() {
        return departments;
    }

    public void setDepartments(List<ResourceRepresentationLocation> departments) {
        this.departments = departments;
    }

}
