package com.zuehlke.pgadmissions.rest.representation.resource;

public class InstitutionClientRepresentation extends ResourceParentRepresentation implements ResourceClientRepresentation {

    private ResourceProcessingRepresentation resourceProcessing;

    @Override
    public ResourceProcessingRepresentation getResourceProcessing() {
        return resourceProcessing;
    }

    @Override
    public void setResourceProcessing(ResourceProcessingRepresentation resourceProcessing) {
        this.resourceProcessing = resourceProcessing;
    }

}
