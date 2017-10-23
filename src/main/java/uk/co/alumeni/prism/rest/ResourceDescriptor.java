package uk.co.alumeni.prism.rest;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;

public class ResourceDescriptor {

    private Class<? extends Resource> type;

    private Class<? extends ResourceRepresentationSimple> representationType;

    private PrismScope resourceScope;

    public ResourceDescriptor(Class<? extends Resource> type, Class<? extends ResourceRepresentationSimple> representationType, PrismScope resourceScope) {
        this.type = type;
        this.representationType = representationType;
        this.resourceScope = resourceScope;
    }

    public Class<? extends Resource> getType() {
        return type;
    }

    public Class<? extends ResourceRepresentationSimple> getRepresentationType() {
        return representationType;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

}
