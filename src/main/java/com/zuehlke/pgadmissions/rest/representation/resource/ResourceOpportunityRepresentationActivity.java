package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class ResourceOpportunityRepresentationActivity extends ResourceRepresentationActivity {

    private PrismOpportunityType opportunityType;

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

}
