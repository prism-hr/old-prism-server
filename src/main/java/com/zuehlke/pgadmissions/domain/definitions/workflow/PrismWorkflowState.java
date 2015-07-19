package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class PrismWorkflowState {

	protected PrismState state;

	protected List<PrismStateAction> stateActions = Lists.newArrayList();

	protected HashMap<PrismAction, PrismStateAction> stateActionsByAction = Maps.newHashMap();

	public PrismWorkflowState initialize(PrismState state) {
		this.state = state;
		setStateActions();
		indexStateActionsByAction();
		return this;
	}

	public List<PrismStateAction> getStateActions() {
		return stateActions;
	}

	public PrismStateAction getStateActionsByAction(PrismAction action) {
		return stateActionsByAction.get(action);
	}

	protected abstract void setStateActions();

	private void indexStateActionsByAction() {
		for (PrismStateAction stateAction : stateActions) {
			stateActionsByAction.put(stateAction.getAction(), stateAction);
			
			PrismAction actionOther = stateAction.getActionOther();
			if (actionOther != null) {
			    stateActionsByAction.put(actionOther, stateAction);
			}
		}
	}

}
