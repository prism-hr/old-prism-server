package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismMotivationContext {

    APPLICANT(PROJECT, PROGRAM), //
    UNIVERSITY(DEPARTMENT, INSTITUTION), //
    EMPLOYER(DEPARTMENT, INSTITUTION);

    private PrismScope[] filterScopes;

    private PrismMotivationContext(PrismScope... filterScopes) {
        this.filterScopes = filterScopes;
    }

    public PrismScope[] getFilterScopes() {
        return filterScopes;
    }

}
