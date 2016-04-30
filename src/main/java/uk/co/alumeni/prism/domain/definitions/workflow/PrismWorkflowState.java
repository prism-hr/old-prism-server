package uk.co.alumeni.prism.domain.definitions.workflow;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.HashMap;
import java.util.Set;

public abstract class PrismWorkflowState {

    protected PrismState state;

    protected Set<PrismStateAction> stateActions = newLinkedHashSet();

    protected HashMap<PrismAction, PrismStateAction> stateActionsByAction = newHashMap();

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
