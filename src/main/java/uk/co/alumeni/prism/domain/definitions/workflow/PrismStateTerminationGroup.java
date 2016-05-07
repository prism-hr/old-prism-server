package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_MESSAGING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_MESSAGING_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;

public enum PrismStateTerminationGroup {

    APPLICATION_TERMINATE_REFERENCE_GROUP(
            new PrismStateTermination() //
                    .withTerminationState(APPLICATION_REFERENCE)),

    APPLICATION_TERMINATE_MESSAGING_GROUP(
            new PrismStateTermination() //
                    .withTerminationState(APPLICATION_MESSAGING),
            new PrismStateTermination() //
                    .withTerminationState(APPLICATION_MESSAGING_PENDING_COMPLETION));

    private PrismStateTermination[] stateTerminations;

    private PrismStateTerminationGroup(PrismStateTermination... stateTerminations) {
        this.stateTerminations = stateTerminations;
    }

    public PrismStateTermination[] getStateTerminations() {
        return stateTerminations;
    }

}
