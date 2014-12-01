package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;

public class ResourceListFilterDTO extends ListFilterDTO {

    private Boolean urgentOnly;

    private List<ResourceListFilterConstraintDTO> constraints;

    public final Boolean getUrgentOnly() {
        return urgentOnly;
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
        setMatchMode(matchMode);
        return this;
    }
    
    public ResourceListFilterDTO withSortOrder(FilterSortOrder sortOrder) {
        setSortOrder(sortOrder);
        return this;
    }
    
    public ResourceListFilterDTO withValueString(String valueString) {
        setValueString(valueString);
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
        return hasConstraints() && getValueString() != null;
    }

}
