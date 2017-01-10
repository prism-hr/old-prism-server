package uk.co.alumeni.prism.domain.definitions.workflow;

import java.util.Arrays;
import java.util.List;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;

public enum PrismActionCondition {

    ACCEPT_APPLICATION(INSTITUTION, DEPARTMENT, PROGRAM, PROJECT), //
    ACCEPT_PROJECT(INSTITUTION, DEPARTMENT, PROGRAM), //
    ACCEPT_PROGRAM(INSTITUTION, DEPARTMENT), //
    ACCEPT_DEPARTMENT(INSTITUTION);

    private List<PrismScope> validScopes;

    PrismActionCondition(PrismScope... validScopes) {
        this.validScopes = Arrays.asList(validScopes);
    }

    public List<PrismScope> getValidScopes() {
        return validScopes;
    }

}
