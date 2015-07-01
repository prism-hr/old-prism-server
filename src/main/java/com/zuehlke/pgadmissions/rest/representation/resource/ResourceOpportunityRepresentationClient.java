package com.zuehlke.pgadmissions.rest.representation.resource;

public class ResourceOpportunityRepresentationClient extends ResourceOpportunityRepresentation implements ResourceRepresentationClient {

    private ResourceSummaryRepresentation resourceSummary;

    @Override
    public ResourceSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    @Override
    public void setResourceSummary(ResourceSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }

}
