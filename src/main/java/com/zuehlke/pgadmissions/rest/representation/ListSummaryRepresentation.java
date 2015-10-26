package com.zuehlke.pgadmissions.rest.representation;

public class ListSummaryRepresentation {

    private String opportunityCategory;

    private Integer rowCount;

    public String getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(String opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public ListSummaryRepresentation withOpportunityCategory(String opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
        return this;
    }

    public ListSummaryRepresentation withRowCount(Integer rowCount) {
        this.rowCount = rowCount;
        return this;
    }

}
