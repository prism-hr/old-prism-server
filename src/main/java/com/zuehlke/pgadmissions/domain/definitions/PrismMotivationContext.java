package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import java.util.LinkedList;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismMotivationContext {

    APPLICANT(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)
                    .withScope(PROGRAM))
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)
                    .withScope(PROGRAM)
                    .withScope(PROJECT)),
            PROGRAM, PROJECT),
    UNIVERSITY(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)),
            INSTITUTION, DEPARTMENT),
    EMPLOYER(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION)
                    .withScope(PROJECT)),
            INSTITUTION, DEPARTMENT);

    private PrismScopeRelations permittedRelations;

    private PrismScope[] filterScopes;

    PrismMotivationContext(PrismScopeRelations permittedRelations, PrismScope... filterScopes) {
        this.permittedRelations = permittedRelations;
        this.filterScopes = filterScopes;
    }

    public PrismScopeRelations getPermittedRelations() {
        return permittedRelations;
    }

    public PrismScope[] getFilterScopes() {
        return filterScopes;
    }

    public static class PrismScopeRelations extends LinkedList<PrismScopeRelation> {

        public PrismScopeRelations withScopeCreationFamily(PrismScopeRelation scopeCreationFamily) {
            super.add(scopeCreationFamily);
            return this;
        }

    }

    public static class PrismScopeRelation extends LinkedList<PrismScope> {

        public PrismScopeRelation withScope(PrismScope scope) {
            super.add(scope);
            return this;
        }

    }

}
