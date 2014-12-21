package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismProgramDisabledCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)  //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withActionEnhancement(PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR)))); //
    }

}
