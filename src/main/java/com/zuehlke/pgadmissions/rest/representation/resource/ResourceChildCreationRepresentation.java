package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

import java.util.List;

public class ResourceChildCreationRepresentation extends ResourceRepresentationSimple {

    private PrismOpportunityType opportunityType;

    private Boolean partnerMode;

    private List<ResourceChildCreationRepresentation> children;

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public Boolean getPartnerMode() {
        return partnerMode;
    }

    public void setPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
    }

    public List<ResourceChildCreationRepresentation> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceChildCreationRepresentation> children) {
        this.children = children;
    }

    public ResourceChildCreationRepresentation withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceChildCreationRepresentation withName(String name) {
        setName(name);
        return this;
    }

    public ResourceChildCreationRepresentation withOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
        return this;
    }

    public ResourceChildCreationRepresentation withPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
        return this;
    }

}
