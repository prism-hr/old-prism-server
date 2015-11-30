package uk.co.alumeni.prism.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

public enum PrismActionCondition {

    ACCEPT_APPLICATION(PrismScope.INSTITUTION, PrismScope.DEPARTMENT, PrismScope.PROGRAM, PrismScope.PROJECT),
    ACCEPT_PROJECT(PrismScope.INSTITUTION, PrismScope.DEPARTMENT, PrismScope.PROGRAM),
    ACCEPT_PROGRAM(PrismScope.INSTITUTION, PrismScope.DEPARTMENT),
    ACCEPT_DEPARTMENT(PrismScope.INSTITUTION);

    private List<PrismScope> validScopes;

    PrismActionCondition(PrismScope... validScopes) {
        this.validScopes = Arrays.asList(validScopes);
    }

    public List<PrismScope> getValidScopes() {
        return validScopes;
    }

}
