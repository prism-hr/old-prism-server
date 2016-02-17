package uk.co.alumeni.prism.domain.definitions.workflow;

public enum PrismStateTerminationGroup {

    APPLICATION_TERMINATE_REFERENCE_GROUP(new PrismStateTermination() //
            .withTerminationState(PrismState.APPLICATION_REFERENCE));

    private PrismStateTermination[] stateTerminations;

    private PrismStateTerminationGroup(PrismStateTermination... stateTerminations) {
        this.stateTerminations = stateTerminations;
    }

    public PrismStateTermination[] getStateTerminations() {
        return stateTerminations;
    }

}
