package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class ResourceChildCreationRepresentation extends ResourceRepresentationSimple {

    private PrismOpportunityType opportunityType;

    private Boolean partnerMode;

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
