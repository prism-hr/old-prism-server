package com.zuehlke.pgadmissions.rest;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;

public class ResourceDescriptor {

    private Class<? extends Resource<?>> type;

    private Class<? extends ResourceRepresentationSimple> representationType;

    private PrismScope resourceScope;

    public ResourceDescriptor(Class<? extends Resource<?>> type, Class<? extends ResourceRepresentationSimple> representationType, PrismScope resourceScope) {
        this.type = type;
        this.representationType = representationType;
        this.resourceScope = resourceScope;
    }

    public Class<? extends Resource<?>> getType() {
        return type;
    }

    public Class<? extends ResourceRepresentationSimple> getRepresentationType() {
        return representationType;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

}
