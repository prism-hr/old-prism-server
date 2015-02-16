package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class StateActionDTO {

    private PrismState stateId;

    private PrismAction actionId;

    public final PrismState getStateId() {
        return stateId;
    }

    public final void setStateId(PrismState stateId) {
        this.stateId = stateId;
    }

    public final PrismAction getActionId() {
        return actionId;
    }

    public final void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

}
