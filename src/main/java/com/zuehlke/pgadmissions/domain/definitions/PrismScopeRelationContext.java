package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.LinkedList;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismScopeRelationContext {
    
    QUALIFICATION(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION, true, false, false)
                    .withScope(DEPARTMENT, true, false, false)
                    .withScope(PROGRAM, true, false, false))
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION, true, false, false)
                    .withScope(DEPARTMENT, true, false, false)
                    .withScope(PROGRAM, true, false, false)
                    .withScope(PROJECT, false, true, null))),
    EMPLOYER(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION, true, false, false))),
    REFEREE(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION, true, false, null)
                    .withScope(PROJECT, false, true, true)));

    private PrismScopeRelations relations;
    
    private PrismScopeRelationContext(PrismScopeRelations creations) {
        this.relations = creations;
    }

    public PrismScopeRelations getRelations() {
        return relations;
    }

    public static class PrismScopeRelations extends LinkedList<PrismScopeRelation> {

        private static final long serialVersionUID = 3471008540918552492L;

        public PrismScopeRelations withScopeCreationFamily(PrismScopeRelation scopeCreationFamily) {
            super.add(scopeCreationFamily);
            return this;
        }

    }

    public static class PrismScopeRelation extends LinkedList<PrismScopeCreation> {

        private static final long serialVersionUID = -4310091481554527257L;

        public PrismScopeRelation withScope(PrismScope scope, Boolean autosuggest, Boolean description, Boolean user) {
            super.add(new PrismScopeCreation(scope, autosuggest, description, user));
            return this;
        }

    }

    public static class PrismScopeCreation {

        private PrismScope scope;

        private Boolean autosuggest;

        private Boolean description;

        private Boolean user;

        public PrismScopeCreation(PrismScope scope) {
            this.scope = scope;
        }

        public PrismScopeCreation(PrismScope scope, Boolean autosuggest, Boolean description, Boolean user) {
            this.scope = scope;
            this.autosuggest = autosuggest;
            this.description = description;
            this.user = user;
        }

        public PrismScope getScope() {
            return scope;
        }

        public void setScope(PrismScope scope) {
            this.scope = scope;
        }

        public Boolean getAutosuggest() {
            return autosuggest;
        }

        public void setAutosuggest(Boolean autosuggest) {
            this.autosuggest = autosuggest;
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
