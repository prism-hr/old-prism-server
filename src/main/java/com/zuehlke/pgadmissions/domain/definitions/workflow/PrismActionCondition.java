package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

public enum PrismActionCondition {

    ACCEPT_PROGRAM(PrismScope.INSTITUTION),
    ACCEPT_PROJECT(PrismScope.INSTITUTION, PrismScope.PROGRAM),
    ACCEPT_APPLICATION(PrismScope.INSTITUTION, PrismScope.PROGRAM, PrismScope.PROJECT);

    private List<PrismScope> validScopes;

    PrismActionCondition(PrismScope... validScopes) {
        this.validScopes = Arrays.asList(validScopes);
    }

    public List<PrismScope> getValidScopes() {
        return validScopes;
    }
}
