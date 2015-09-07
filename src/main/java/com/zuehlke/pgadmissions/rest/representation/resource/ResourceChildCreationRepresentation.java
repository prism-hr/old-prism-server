package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceChildCreationRepresentation extends ResourceRepresentationIdentity {

    private Boolean externalMode;

    private List<ResourceChildCreationRepresentation> childResources;

    public void setExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
    }

    public void setChildResources(List<ResourceChildCreationRepresentation> childResources) {
        this.childResources = childResources;
    }

    public Boolean getExternalMode() {
        return externalMode;
    }

    public List<ResourceChildCreationRepresentation> getChildResources() {
        return childResources;
    }

    public ResourceChildCreationRepresentation withScope(PrismScope scope) {
        setScope(scope);
        return this;
    }

    public ResourceChildCreationRepresentation withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceChildCreationRepresentation withName(String name) {
        setName(name);
        return this;
    }

    public ResourceChildCreationRepresentation withExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
        return this;
    }

    public ResourceChildCreationRepresentation addChildResource(ResourceChildCreationRepresentation childResource) {
        this.childResources = this.childResources == null ? Lists.newLinkedList() : this.childResources;
        this.childResources.add(childResource);
        return this;
    }

}
