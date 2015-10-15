package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext.EMPLOYER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext.UNIVERSITY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.EXPERIENCE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.PERSONAL_DEVELOPMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.STUDY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory.WORK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.LinkedList;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismScopeRelationContext {

    QUALIFICATION(UNIVERSITY, new PrismScopeRelationGroup()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION, true, false, false)
                    .withScope(DEPARTMENT, true, false, false)
                    .withScope(PROGRAM, true, false, false, STUDY, PERSONAL_DEVELOPMENT))
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION, true, false, false)
                    .withScope(DEPARTMENT, true, false, false)
                    .withScope(PROGRAM, true, false, false, STUDY, PERSONAL_DEVELOPMENT)
                    .withScope(PROJECT, false, true, null))), //
    EMPLOYMENT_POSITION(EMPLOYER, new PrismScopeRelationGroup()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION, true, false, false)
                    .withScope(PROJECT, false, true, false, EXPERIENCE, WORK))), //
    REFEREE(EMPLOYER, new PrismScopeRelationGroup()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION, true, false, null)
                    .withScope(PROJECT, false, false, true, WORK)));

    private PrismMotivationContext context;

    private PrismScopeRelationGroup relations;

    private PrismScopeRelationContext(PrismMotivationContext context, PrismScopeRelationGroup creations) {
        this.context = context;
        this.relations = creations;
    }

    public PrismMotivationContext getContext() {
        return context;
    }

    public PrismScopeRelationGroup getRelations() {
        return relations;
    }

    public static class PrismScopeRelationGroup extends LinkedList<PrismScopeRelation> {

        private static final long serialVersionUID = 3471008540918552492L;

        public PrismScopeRelationGroup withScopeCreationFamily(PrismScopeRelation scopeCreationFamily) {
            super.add(scopeCreationFamily);
            return this;
        }

    }

    public static class PrismScopeRelation extends LinkedList<PrismScopeCreation> {

        private static final long serialVersionUID = -4310091481554527257L;

        public PrismScopeRelation withScope(PrismScope scope, Boolean autoSuggest, Boolean description, Boolean user, PrismOpportunityCategory... opportunityCategories) {
            super.add(new PrismScopeCreation(scope, autoSuggest, description, user, opportunityCategories));
            return this;
        }

    }

    public static class PrismScopeCreation {

        private PrismScope scope;

        private PrismOpportunityCategory[] opportunityCategories;

        private Boolean autoSuggest;

        private Boolean description;

        private Boolean user;

        public PrismScopeCreation(PrismScope scope, Boolean autoSuggest, Boolean description, Boolean user, PrismOpportunityCategory... opportunityCategories) {
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
