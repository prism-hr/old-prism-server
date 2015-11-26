package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;

public class ResourceOpportunityRepresentationRelation extends ResourceRepresentationRelation {

    private PrismOpportunityType opportunityType;

    private String summary;

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

}
