package uk.co.alumeni.prism.rest.dto;

import uk.co.alumeni.prism.domain.definitions.PrismFilterMatchMode;
import uk.co.alumeni.prism.domain.definitions.PrismFilterSortOrder;

public class ListFilterDTO {

    private PrismFilterMatchMode matchMode;

    private PrismFilterSortOrder sortOrder;
    
    private String valueString;

    public PrismFilterMatchMode getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(PrismFilterMatchMode matchMode) {
        this.matchMode = matchMode;
    }

    public PrismFilterSortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(PrismFilterSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

}
