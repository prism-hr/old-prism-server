package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceChildCreationRepresentation extends ResourceRepresentationIdentity {

    private Boolean partnerMode;

    private List<ResourceChildCreationRepresentation> childResources;

    public void setPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
    }

    public void setChildResources(List<ResourceChildCreationRepresentation> childResources) {
        this.childResources = childResources;
    }

    public Boolean getPartnerMode() {
        return partnerMode;
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

    public ResourceChildCreationRepresentation withPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
        return this;
    }

    public ResourceChildCreationRepresentation addChildResource(ResourceChildCreationRepresentation childResource) {
        this.childResources = this.childResources == null ? Lists.newLinkedList() : this.childResources;
        this.childResources.add(childResource);
        return this;
    }

}
