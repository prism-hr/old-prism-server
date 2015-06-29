package com.zuehlke.pgadmissions.rest.representation.resource;

public class ResourceOpportunityClientRepresentation extends ResourceOpportunityRepresentation implements ResourceClientRepresentation {

    private ResourceSummaryRepresentation resourceSummary;

    public ResourceSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public void setResourceSummary(ResourceSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }

}
