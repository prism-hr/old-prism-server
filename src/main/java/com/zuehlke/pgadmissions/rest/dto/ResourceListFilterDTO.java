package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;

public class ResourceListFilterDTO {

    private Boolean urgentOnly;

    private FilterMatchMode matchMode;

    private FilterSortOrder sortOrder;

    private String valueString;

    private List<ResourceListFilterConstraintDTO> constraints;

    public final Boolean getUrgentOnly() {
        return urgentOnly;
    }

    public final FilterMatchMode getMatchMode() {
        return matchMode;
    }

    public final FilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public final String getValueString() {
        return valueString;
    }

    public final List<ResourceListFilterConstraintDTO> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ResourceListFilterConstraintDTO> constraints) {
        this.constraints = constraints;
    }

    public ResourceListFilterDTO withConstraints(List<ResourceListFilterConstraintDTO> constraints) {
        this.constraints = constraints;
        return this;
    }

    public ResourceListFilterDTO withUrgentOnly(Boolean urgentOnly) {
        this.urgentOnly = urgentOnly;
        return this;
    }

    public ResourceListFilterDTO withMatchMode(FilterMatchMode matchMode) {
        this.matchMode = matchMode;
        return this;
    }

    public ResourceListFilterDTO withSortOrder(FilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public ResourceListFilterDTO withValueString(String valueString) {
        this.valueString = valueString;
        return this;
    }

    public void addConstraint(ResourceListFilterConstraintDTO constraint) {
        constraints.add(constraint);
    }

    public boolean isUrgentOnly() {
        return BooleanUtils.toBoolean(urgentOnly);
    }

    public boolean hasConstraints() {
        return constraints != null && !constraints.isEmpty();
    }
    
    public boolean hasBasicFilter() {
        return hasConstraints() && valueString != null;
    }    

}
