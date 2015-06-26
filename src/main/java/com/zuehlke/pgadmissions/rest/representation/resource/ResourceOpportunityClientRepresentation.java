package com.zuehlke.pgadmissions.rest.representation.resource;

public class ResourceOpportunityClientRepresentation extends ResourceOpportunityRepresentation {

    private ResourceProcessingRepresentation resourceProcessing;

    public ResourceProcessingRepresentation getResourceProcessing() {
        return resourceProcessing;
    }

    public void setResourceProcessing(ResourceProcessingRepresentation resourceProcessing) {
        this.resourceProcessing = resourceProcessing;
    }

}
