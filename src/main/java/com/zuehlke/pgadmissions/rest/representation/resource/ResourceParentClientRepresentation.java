package com.zuehlke.pgadmissions.rest.representation.resource;

public class ResourceParentClientRepresentation extends ResourceParentRepresentation implements ResourceClientRepresentation {

    private ResourceSummaryRepresentation resourceSummary;

    public ResourceSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public void setResourceSummary(ResourceSummaryRepresentation resourceProcessing) {
        this.resourceSummary = resourceProcessing;
    }

}
