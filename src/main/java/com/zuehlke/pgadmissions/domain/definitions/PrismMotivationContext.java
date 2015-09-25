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
                    .withScope(PROJECT))), //
    UNIVERSITY(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT))), //
    EMPLOYER(new PrismScopeRelations()
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION))
            .withScopeCreationFamily(new PrismScopeRelation()
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT))); //
    
    private PrismScopeRelations permittedRelations;

    private PrismMotivationContext(PrismScopeRelations permittedRelations) {
        this.permittedRelations = permittedRelations;
    }

    public PrismScopeRelations getPermittedRelations() {
        return permittedRelations;
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
