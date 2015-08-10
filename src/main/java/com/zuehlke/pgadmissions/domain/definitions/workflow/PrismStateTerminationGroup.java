package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VERIFICATION;

public enum PrismStateTerminationGroup {

    APPLICATION_TERMINATE_REFERENCE_GROUP(new PrismStateTermination() //
            .withTerminationState(APPLICATION_REFERENCE)), //

    APPLICATION_TERMINATE_VERIFICATION_GROUP(new PrismStateTermination() //
            .withTerminationState(APPLICATION_VERIFICATION)), //

    APPLICATION_TERMINATE_ALL_STATES_GROUP( //
            new PrismStateTermination() //
                    .withTerminationState(APPLICATION_REFERENCE), //
            new PrismStateTermination() //
                    .withTerminationState(APPLICATION_VERIFICATION));

    private PrismStateTermination[] stateTerminations;

    private PrismStateTerminationGroup(PrismStateTermination... stateTerminations) {
        this.stateTerminations = stateTerminations;
    }

    public PrismStateTermination[] getStateTerminations() {
        return stateTerminations;
    }

}
