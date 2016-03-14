package uk.co.alumeni.prism.domain.definitions.workflow;

import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class PrismWorkflowState {

    protected PrismState state;

    protected Set<PrismStateAction> stateActions = Sets.newHashSet();

    protected HashMap<PrismAction, PrismStateAction> stateActionsByAction = Maps.newHashMap();

    public PrismWorkflowState initialize(PrismState state) {
        this.state = state;
        setStateActions();
        indexStateActionsByAction();
        return this;
    }

    public Set<PrismStateAction> getStateActions() {
        return stateActions;
    }

    public PrismStateAction getStateActionsByAction(PrismAction action) {
        return stateActionsByAction.get(action);
    }

    protected abstract void setStateActions();

    private void indexStateActionsByAction() {
        for (PrismStateAction stateAction : stateActions) {
            stateActionsByAction.put(stateAction.getAction(), stateAction);
        }
    }

}
