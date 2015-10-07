package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.LinkedList;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

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
            PROGRAM, PROJECT), //
    UNIVERSITY(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)),
            INSTITUTION, DEPARTMENT), //
    EMPLOYER(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION))
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)),
            INSTITUTION, DEPARTMENT); //
    
    private PrismScopeRelations permittedRelations;

    private PrismScope[] filterScopes;
    
    private PrismMotivationContext(PrismScopeRelations permittedRelations, PrismScope... filterScopes) {
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

        private static final long serialVersionUID = 7042394502685379016L;

        public PrismScopeRelations withScopeCreationFamily(PrismScopeRelation scopeCreationFamily) {
            super.add(scopeCreationFamily);
            return this;
        }

    }

    public static class PrismScopeRelation extends LinkedList<PrismScope> {

        private static final long serialVersionUID = -6696268838881348249L;

        public PrismScopeRelation withScope(PrismScope scope) {
            super.add(scope);
            return this;
        }

    }

}
