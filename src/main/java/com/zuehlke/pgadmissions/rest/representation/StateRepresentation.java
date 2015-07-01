package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

public class StateRepresentation {

    private PrismState id;

    private PrismStateGroup stateGroup;

    public StateRepresentation(PrismState id, PrismStateGroup stateGroup) {
        this.id = id;
        this.stateGroup = stateGroup;
    }

    public PrismState getId() {
        return id;
    }

    public PrismStateGroup getStateGroup() {
        return stateGroup;
    }
}
