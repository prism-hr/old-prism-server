package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class OpportunityCategoryRepresentation {

    private PrismOpportunityCategory opportunityCategory;
    
    private boolean published;
    
    private List<OpportunityTypeRepresentation> opportunityTypes;

    public OpportunityCategoryRepresentation(PrismOpportunityCategory opportunityCategory, boolean published, List<OpportunityTypeRepresentation> opportunityTypes) {
        this.opportunityCategory = opportunityCategory;
        this.published = published;
        this.opportunityTypes = opportunityTypes;
    }

    public PrismOpportunityCategory getOpportunityCategory() {
        return opportunityCategory;
    }

    public void setOpportunityCategory(PrismOpportunityCategory opportunityCategory) {
        this.opportunityCategory = opportunityCategory;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
    
    public List<OpportunityTypeRepresentation> getOpportunityTypes() {
        return opportunityTypes;
    }

    public void setOpportunityTypes(List<OpportunityTypeRepresentation> opportunityTypes) {
        this.opportunityTypes = opportunityTypes;
    }

    public static class OpportunityTypeRepresentation {

        private PrismOpportunityType opportunityType;

        private boolean published;

        public OpportunityTypeRepresentation(PrismOpportunityType opportunityType, boolean published) {
            this.opportunityType = opportunityType;
            this.published = published;
        }

        public PrismOpportunityType getOpportunityType() {
            return opportunityType;
        }

        public void setOpportunityType(PrismOpportunityType opportunityType) {
            this.opportunityType = opportunityType;
        }

        public boolean isPublished() {
            return published;
        }

        public void setPublished(boolean published) {
            this.published = published;
        }

    }

}
