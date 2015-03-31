package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismStateTerminationGroup {

	APPLICATION_TERMINATE_GROUP( //
	        new PrismStateTermination() //
	                .withTerminationState(PrismState.APPLICATION_REFERENCE), //
	        new PrismStateTermination() //
	                .withTerminationState(PrismState.APPLICATION_VERIFICATION));

	private PrismStateTermination[] stateTerminations;

	private PrismStateTerminationGroup(PrismStateTermination... stateTerminations) {
		this.stateTerminations = stateTerminations;
	}

	public PrismStateTermination[] getStateTerminations() {
		return stateTerminations;
	}

}