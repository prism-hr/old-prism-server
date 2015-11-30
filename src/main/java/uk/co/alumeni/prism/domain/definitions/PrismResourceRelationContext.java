package uk.co.alumeni.prism.domain.definitions;

import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.PERSONAL_DEVELOPMENT;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.STUDY;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.WORK;

import java.util.LinkedList;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

public enum PrismResourceRelationContext {

    QUALIFICATION(PrismResourceContext.UNIVERSITY, new PrismResourceRelationGroup()
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, false)
                    .withScope(PrismScope.DEPARTMENT, true, false, false)
                    .withScope(PrismScope.PROGRAM, true, false, false, STUDY, PERSONAL_DEVELOPMENT))
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, false)
                    .withScope(PrismScope.DEPARTMENT, true, false, false)
                    .withScope(PrismScope.PROGRAM, true, false, false, STUDY, PERSONAL_DEVELOPMENT)
                    .withScope(PrismScope.PROJECT, false, true, null))), //
    EMPLOYMENT_POSITION(PrismResourceContext.EMPLOYER, new PrismResourceRelationGroup()
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, false)
                    .withScope(PrismScope.PROJECT, false, true, false, EXPERIENCE, WORK))), //
    REFEREE(PrismResourceContext.EMPLOYER, new PrismResourceRelationGroup()
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, false)
                    .withScope(PrismScope.PROJECT, false, false, true, WORK))),
    EMPLOYER_RELATION(PrismResourceContext.EMPLOYER, new PrismResourceRelationGroup()
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, true))
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, true)
                    .withScope(PrismScope.DEPARTMENT, true, false, true))),
    UNIVERSITY_RELATION(PrismResourceContext.UNIVERSITY, new PrismResourceRelationGroup()
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, true))
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, true)
                    .withScope(PrismScope.DEPARTMENT, true, false, true))),
    ADVERT_LOCATION(null, new PrismResourceRelationGroup()
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, false))
            .withScopeCreationFamily(new PrismResourceRelation()
                    .withScope(PrismScope.INSTITUTION, true, false, false)
                    .withScope(PrismScope.DEPARTMENT, true, false, false)));

    private PrismResourceContext context;

    private PrismResourceRelationGroup relations;

    private PrismResourceRelationContext(PrismResourceContext context, PrismResourceRelationGroup creations) {
        this.context = context;
        this.relations = creations;
    }

    public PrismResourceContext getContext() {
        return context;
    }

    public PrismResourceRelationGroup getRelations() {
        return relations;
    }

    public static class PrismResourceRelationGroup extends LinkedList<PrismResourceRelation> {

        private static final long serialVersionUID = 3471008540918552492L;

        public PrismResourceRelationGroup withScopeCreationFamily(PrismResourceRelation scopeCreationFamily) {
            super.add(scopeCreationFamily);
            return this;
        }

    }

    public static class PrismResourceRelation extends LinkedList<PrismResourceCreation> {

        private static final long serialVersionUID = -4310091481554527257L;

        public PrismResourceRelation withScope(PrismScope scope, Boolean autoSuggest, Boolean description, Boolean user, PrismOpportunityCategory... opportunityCategories) {
            super.add(new PrismResourceCreation(scope, autoSuggest, description, user, opportunityCategories));
            return this;
        }

    }

    public static class PrismResourceCreation {

        private PrismScope scope;

        private PrismOpportunityCategory[] opportunityCategories;

        private Boolean autoSuggest;

        private Boolean description;

        private Boolean user;

        public PrismResourceCreation(PrismScope scope, Boolean autoSuggest, Boolean description, Boolean user, PrismOpportunityCategory... opportunityCategories) {
            this.scope = scope;
            this.autoSuggest = autoSuggest;
            this.description = description;
            this.user = user;
            this.opportunityCategories = opportunityCategories;
        }

        public PrismScope getScope() {
            return scope;
        }

        public void setScope(PrismScope scope) {
            this.scope = scope;
        }

        public PrismOpportunityCategory[] getOpportunityCategories() {
            return opportunityCategories;
        }

        public void setOpportunityCategories(PrismOpportunityCategory[] opportunityCategories) {
            this.opportunityCategories = opportunityCategories;
        }

        public Boolean getAutoSuggest() {
            return autoSuggest;
        }

        public void setAutoSuggest(Boolean autoSuggest) {
            this.autoSuggest = autoSuggest;
        }

        public Boolean getDescription() {
            return description;
        }

        public void setDescription(Boolean description) {
            this.description = description;
        }

        public Boolean getUser() {
            return user;
        }

        public void setUser(Boolean user) {
            this.user = user;
        }

    }

}
