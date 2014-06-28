package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;

public abstract class PrismWorkflowState {

    protected List<PrismStateAction> stateActions = Lists.newArrayList();
    
    public List<PrismStateAction> getStateActions() {
        setStateActions();
        return stateActions;
    }
    
    protected abstract void setStateActions();
    
}
