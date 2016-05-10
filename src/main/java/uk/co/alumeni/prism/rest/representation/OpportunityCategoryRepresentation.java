package uk.co.alumeni.prism.rest.representation;

import java.util.List;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;

public class OpportunityCategoryRepresentation {

    private PrismOpportunityCategory id;

    private boolean published;

    private boolean defaultPermanent;

    private boolean permittedOnCourse;

    private List<OpportunityTypeRepresentation> opportunityTypes;

    public OpportunityCategoryRepresentation(PrismOpportunityCategory id, boolean published, boolean defaultPermanent, boolean permittedOnCourse,
            List<OpportunityTypeRepresentation> opportunityTypes) {
        this.id = id;
        this.published = published;
        this.defaultPermanent = defaultPermanent;
        this.permittedOnCourse = permittedOnCourse;
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

    public boolean isDefaultPermanent() {
        return defaultPermanent;
    }

    public void setDefaultPermanent(boolean defaultPermanent) {
        this.defaultPermanent = defaultPermanent;
    }

    public boolean isPermittedOnCourse() {
        return permittedOnCourse;
    }

    public void setPermittedOnCourse(boolean permittedOnCourse) {
        this.permittedOnCourse = permittedOnCourse;
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

        private PrismDisplayPropertyDefinition description;

        public OpportunityTypeRepresentation(PrismOpportunityType id, boolean published, PrismDisplayPropertyDefinition termsAndConditions) {
            this.id = id;
            this.published = published;
            this.description = termsAndConditions;
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

        public PrismDisplayPropertyDefinition getDescription() {
            return description;
        }

        public void setDescription(PrismDisplayPropertyDefinition termsAndConditions) {
            this.description = termsAndConditions;
        }

    }

}
