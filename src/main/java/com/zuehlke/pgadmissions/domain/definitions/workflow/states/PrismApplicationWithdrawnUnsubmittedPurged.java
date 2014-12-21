package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismApplicationWithdrawnUnsubmittedPurged extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_CREATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR)))); //
    }

}
