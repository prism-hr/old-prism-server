package uk.co.alumeni.prism.rest.representation.state;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateGroup;

public class StateRepresentationSimple {

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

}
