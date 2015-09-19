package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.LinkedList;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismResourceFamilyCreation {

    QUALIFICATION(new PrismScopeCreationFamilies() //
            .withScopeCreationFamily(new PrismScopeCreationFamily() //
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)
                    .withScope(PROGRAM))
            .withScopeCreationFamily(new PrismScopeCreationFamily() //
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT)
                    .withScope(PROGRAM)
                    .withScope(PROJECT))), //
    UNIVERSITY(new PrismScopeCreationFamilies() //
            .withScopeCreationFamily(new PrismScopeCreationFamily() //
                    .withScope(INSTITUTION)
                    .withScope(DEPARTMENT))), //
    EMPLOYER(new PrismScopeCreationFamilies() //
            .withScopeCreationFamily(new PrismScopeCreationFamily() //
                    .withScope(INSTITUTION)));
    
    private PrismScopeCreationFamilies scopeCreationFamilies;

    private PrismResourceFamilyCreation(PrismScopeCreationFamilies scopeCreations) {
        this.scopeCreationFamilies = scopeCreations;
    }

    public PrismScopeCreationFamilies getScopeCreationFamilies() {
        return scopeCreationFamilies;
    }

    public static class PrismScopeCreationFamilies extends LinkedList<PrismScopeCreationFamily> {

        private static final long serialVersionUID = 7042394502685379016L;

        public PrismScopeCreationFamilies withScopeCreationFamily(PrismScopeCreationFamily scopeCreationFamily) {
            super.add(scopeCreationFamily);
            return this;
        }

    }

    public static class PrismScopeCreationFamily extends LinkedList<PrismScope> {

        private static final long serialVersionUID = -6696268838881348249L;

        public PrismScopeCreationFamily withScope(PrismScope scopeCreation) {
            super.add(scopeCreation);
            return this;
        }

    }

}
