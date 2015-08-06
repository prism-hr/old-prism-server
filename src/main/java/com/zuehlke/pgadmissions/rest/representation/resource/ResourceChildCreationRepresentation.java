package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class ResourceChildCreationRepresentation extends ResourceRepresentationIdentity {

    private PrismOpportunityType opportunityType;

    private Boolean partnerMode;

    private List<ResourceChildCreationRepresentation> childResources;

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public Boolean getPartnerMode() {
        return partnerMode;
    }

    public List<ResourceChildCreationRepresentation> getChildResources() {
        return childResources;
    }

    public ResourceChildCreationRepresentation withPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
        return this;
    }

    public ResourceChildCreationRepresentation addChildResource(ResourceChildCreationRepresentation childResource) {
        if (this.childResources == null) {
            this.childResources = Lists.newLinkedList();
        }
        this.childResources.add(childResource);
        return this;
    }

}
