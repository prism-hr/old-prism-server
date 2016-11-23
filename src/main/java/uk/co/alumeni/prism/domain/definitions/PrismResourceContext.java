package uk.co.alumeni.prism.domain.definitions;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.*;

public enum PrismResourceContext {

    APPLICANT(PROJECT, PROGRAM), //
    UNIVERSITY(DEPARTMENT, INSTITUTION), //
    EMPLOYER(DEPARTMENT, INSTITUTION);

    private PrismScope[] filterScopes;

    private PrismResourceContext(PrismScope... filterScopes) {
        this.filterScopes = filterScopes;
    }

    public PrismScope[] getFilterScopes() {
        return filterScopes;
    }

}
