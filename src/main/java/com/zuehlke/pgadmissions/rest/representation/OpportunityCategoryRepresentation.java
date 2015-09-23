package com.zuehlke.pgadmissions.rest.representation;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;

public class OpportunityCategoryRepresentation {

    private PrismOpportunityCategory id;

    private boolean published;

    private List<OpportunityTypeRepresentation> opportunityTypes;

    public OpportunityCategoryRepresentation(PrismOpportunityCategory id, boolean published, List<OpportunityTypeRepresentation> opportunityTypes) {
        this.id = id;
        this.published = published;
        this.opportunityTypes = opportunityTypes;
    }

    public PrismOpportunityCategory getId() {
        return id;
    }

    public void setId(PrismOpportunityCategory id) {
        this.id = id;
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

        private PrismOpportunityType id;

        private boolean published;

        private boolean requiredEndorsement;

        private PrismDisplayPropertyDefinition termsAndConditions;

        public OpportunityTypeRepresentation(PrismOpportunityType id, boolean published, boolean requiredEndorsement, PrismDisplayPropertyDefinition termsAndConditions) {
            this.id = id;
            this.published = published;
            this.requiredEndorsement = requiredEndorsement;
            this.termsAndConditions = termsAndConditions;
        }

        public PrismOpportunityType getId() {
            return id;
        }

        public void setId(PrismOpportunityType id) {
            this.id = id;
        }

        public boolean isPublished() {
            return published;
        }

        public void setPublished(boolean published) {
            this.published = published;
        }

        public boolean isRequiredEndorsement() {
            return requiredEndorsement;
        }

        public void setRequiredEndorsement(boolean requiredEndorsement) {
            this.requiredEndorsement = requiredEndorsement;
        }

        public PrismDisplayPropertyDefinition getTermsAndConditions() {
            return termsAndConditions;
        }

        public void setTermsAndConditions(PrismDisplayPropertyDefinition termsAndConditions) {
            this.termsAndConditions = termsAndConditions;
        }

    }

}
