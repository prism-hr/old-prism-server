package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;

public enum PrismStateTerminationGroup {

    APPLICATION_TERMINATE_REFERENCE_GROUP(new PrismStateTermination() //
            .withTerminationState(APPLICATION_REFERENCE));

    private PrismStateTermination[] stateTerminations;

    private PrismStateTerminationGroup(PrismStateTermination... stateTerminations) {
        this.stateTerminations = stateTerminations;
    }

    public PrismStateTermination[] getStateTerminations() {
        return stateTerminations;
    }

}
