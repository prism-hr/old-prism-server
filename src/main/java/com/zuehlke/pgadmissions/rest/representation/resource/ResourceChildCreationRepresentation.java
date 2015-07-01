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

    public ResourceChildCreationRepresentation withResourceId(Integer id) {
        setResourceId(id);
        return this;
    }

    public ResourceChildCreationRepresentation withTitle(String title) {
        setTitle(title);
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
