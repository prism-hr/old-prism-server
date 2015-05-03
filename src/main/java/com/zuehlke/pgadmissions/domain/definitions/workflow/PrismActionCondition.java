package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismActionCondition {

    ACCEPT_PROGRAM( //
            Lists.newArrayList(INSTITUTION), //
            Lists.newArrayList(INSTITUTION)), //
    ACCEPT_PROJECT( //
            Lists.newArrayList(INSTITUTION, PROGRAM),
            Lists.newArrayList(INSTITUTION, PROGRAM)), //
    ACCEPT_APPLICATION( //
            Lists.newArrayList(PROGRAM, PROJECT), //
            Lists.newArrayList(INSTITUTION, PROGRAM, PROJECT)), //
    ACCEPT_SPONSOR( //
            Lists.newArrayList(PROJECT), //
            Lists.newArrayList(INSTITUTION, PROGRAM, PROJECT));

    private List<PrismScope> defaultScopes;

    private List<PrismScope> permittedScopes;

    private PrismActionCondition(List<PrismScope> defaultScopes, List<PrismScope> permittedScopes) {
        this.defaultScopes = defaultScopes;
        this.permittedScopes = permittedScopes;
    }

    public List<PrismScope> getDefaultScopes() {
        return defaultScopes;
    }

    public List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }

}
