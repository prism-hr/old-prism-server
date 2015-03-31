package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;

public abstract class PrismWorkflowState {

	protected PrismState state;

	protected List<PrismStateAction> stateActions = Lists.newArrayList();

	protected HashMap<PrismAction, PrismStateAction> stateActionsByAction = Maps.newHashMap();

	protected PrismWorkflowState() {
		setStateActions();
		indexStateActionsByAction();
	}

	public List<PrismStateAction> getStateActions() {
		return stateActions;
	}

	public PrismStateAction getStateActionsByAction(PrismAction action) {
		return stateActionsByAction.get(action);
	}

	public PrismWorkflowState withState(PrismState state) {
		this.state = state;
		return this;
	}

	protected abstract void setStateActions();

	private void indexStateActionsByAction() {
		for (PrismStateAction stateAction : stateActions) {
			stateActionsByAction.put(stateAction.getAction(), stateAction);
		}
	}

}
