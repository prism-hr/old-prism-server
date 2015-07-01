package com.zuehlke.pgadmissions.rest.representation.resource;

public class ResourceParentRepresentationClient extends ResourceParentRepresentation implements ResourceRepresentationClient {

    private ResourceSummaryRepresentation resourceSummary;

    @Override
    public ResourceSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    @Override
    public void setResourceSummary(ResourceSummaryRepresentation resourceProcessing) {
        this.resourceSummary = resourceProcessing;
    }

}
