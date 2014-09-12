package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;

public class ResourceListFilterDTO {

    private Boolean urgentOnly;

    private FilterMatchMode matchMode;

    private FilterSortOrder sortOrder;

    private String valueString;

    private List<ResourceListFilterConstraintDTO> constraints;

    private Boolean saveAsDefaultFilter;

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

    public final Boolean isSaveAsDefaultFilter() {
        return saveAsDefaultFilter;
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

}
