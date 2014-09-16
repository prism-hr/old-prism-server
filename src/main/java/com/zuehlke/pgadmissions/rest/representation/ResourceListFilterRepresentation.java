package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;

import java.util.List;

public class ResourceListFilterRepresentation {

    private Boolean urgentOnly;

    private FilterMatchMode matchMode;

    private FilterSortOrder sortOrder;

    private String valueString;

    private List<ResourceListFilterConstraintRepresentation> constraints;

    private Boolean saveAsDefaultFilter;

    public Boolean getUrgentOnly() {
        return urgentOnly;
    }

    public void setUrgentOnly(Boolean urgentOnly) {
        this.urgentOnly = urgentOnly;
    }

    public FilterMatchMode getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(FilterMatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public FilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(FilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public List<ResourceListFilterConstraintRepresentation> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ResourceListFilterConstraintRepresentation> constraints) {
        this.constraints = constraints;
    }

    public Boolean getSaveAsDefaultFilter() {
        return saveAsDefaultFilter;
    }

    public void setSaveAsDefaultFilter(Boolean saveAsDefaultFilter) {
        this.saveAsDefaultFilter = saveAsDefaultFilter;
    }
}
