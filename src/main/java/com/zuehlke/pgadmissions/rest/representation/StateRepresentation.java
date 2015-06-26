package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class StateRepresentation {

    private PrismState state;

    private PrismStateGroup stateGroup;

    public PrismState getState() {
        return state;
    }

    public void setState(PrismState state) {
        this.state = state;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(PrismStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

    public StateRepresentation withState(PrismState State) {
        this.state = State;
        return this;
    }

    public StateRepresentation withStateGroup(PrismStateGroup stateGroup) {
        this.stateGroup = stateGroup;
        return this;
    }

}
