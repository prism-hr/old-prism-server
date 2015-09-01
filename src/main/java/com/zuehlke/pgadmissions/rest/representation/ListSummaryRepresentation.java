package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;

public class ListSummaryRepresentation {

    private PrismOpportunityCategory opportunityCategory;
    
    private Integer rowCount;

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }
    
    public ListSummaryRepresentation withOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
        return this;
    }

    public ListSummaryRepresentation withRowCount(Integer rowCount) {
        this.rowCount = rowCount;
        return this;
    }
    
}
