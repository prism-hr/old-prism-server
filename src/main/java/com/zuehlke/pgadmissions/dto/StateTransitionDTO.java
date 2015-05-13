package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.State;

public class StateTransitionDTO {

	private State state;
	
	private Action action;
	
	private State transitionState;

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public State getTransitionState() {
		return transitionState;
	}

	public void setTransitionState(State transitionState) {
		this.transitionState = transitionState;
	}
	
}
