package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.FilterMatchMode;
import com.zuehlke.pgadmissions.domain.definitions.FilterSortOrder;

public class ListFilterDTO {

    private FilterMatchMode matchMode;

    private FilterSortOrder sortOrder;

    private String valueString;

    public final FilterMatchMode getMatchMode() {
        return matchMode;
    }

    public final void setMatchMode(FilterMatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public final FilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public final void setSortOrder(FilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public final String getValueString() {
        return valueString;
    }

    public final void setValueString(String valueString) {
        this.valueString = valueString;
    }
    
}
