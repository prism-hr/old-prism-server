package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class StateRepresentation {

    private PrismState id;

    private PrismStateGroup stateGroup;

    public PrismState getId() {
        return id;
    }

    public void setId(PrismState id) {
        this.id = id;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(PrismStateGroup stateGroup) {
        this.stateGroup = stateGroup;
    }

}
