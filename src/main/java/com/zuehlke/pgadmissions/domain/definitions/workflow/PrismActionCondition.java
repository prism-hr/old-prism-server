package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.Arrays;
import java.util.List;

public enum PrismActionCondition {

    ACCEPT_APPLICATION(INSTITUTION, DEPARTMENT, PROGRAM, PROJECT), //
    ACCEPT_PROJECT(INSTITUTION, DEPARTMENT, PROGRAM), //
    ACCEPT_PROGRAM(INSTITUTION, DEPARTMENT),
    ACCEPT_DEPARTMENT(INSTITUTION); //

    private List<PrismScope> validScopes;

    PrismActionCondition(PrismScope... validScopes) {
        this.validScopes = Arrays.asList(validScopes);
    }

    public List<PrismScope> getValidScopes() {
        return validScopes;
    }

}
