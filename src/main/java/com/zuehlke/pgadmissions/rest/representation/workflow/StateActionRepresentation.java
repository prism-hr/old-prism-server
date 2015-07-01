package com.zuehlke.pgadmissions.rest.representation.workflow;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class StateActionRepresentation {

    private PrismState state;

    private PrismAction action;

    private boolean raisesUrgentFlag;

    public StateActionRepresentation(PrismState state, PrismAction action, boolean raisesUrgentFlag) {
        this.state = state;
        this.action = action;
        this.raisesUrgentFlag = raisesUrgentFlag;
    }

    public PrismState getState() {
        return state;
    }

    public PrismAction getAction() {
        return action;
    }

    public boolean isRaisesUrgentFlag() {
        return raisesUrgentFlag;
    }
}
