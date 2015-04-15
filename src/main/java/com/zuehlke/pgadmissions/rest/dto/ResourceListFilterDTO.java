package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import org.apache.commons.lang.BooleanUtils;

import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterSortOrder;

public class ResourceListFilterDTO {

    private PrismResourceListFilterMatchMode matchMode;

    private PrismResourceListFilterSortOrder sortOrder;

    private String valueString;

    private Boolean urgentOnly;

    private List<ResourceListFilterConstraintDTO> constraints;

    public final PrismResourceListFilterMatchMode getMatchMode() {
        return matchMode;
    }

    public final void setMatchMode(PrismResourceListFilterMatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public final PrismResourceListFilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public final void setSortOrder(PrismResourceListFilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public final String getValueString() {
        return valueString;
    }

    public final void setValueString(String valueString) {
        this.valueString = valueString;
    }

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

    public ResourceListFilterDTO withMatchMode(PrismResourceListFilterMatchMode matchMode) {
        this.matchMode = matchMode;
        return this;
    }

    public ResourceListFilterDTO withSortOrder(PrismResourceListFilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
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
