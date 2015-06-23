package com.zuehlke.pgadmissions.rest.representation.resource;

public interface ResourceClientRepresentation {

    public abstract ResourceProcessingRepresentation getResourceProcessing();
    
    public abstract void setResourceProcessing(ResourceProcessingRepresentation resourceProcessing);
    
}
