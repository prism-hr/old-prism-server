package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismProgramApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CORRECT) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(true) //
            .withNotificationTemplate(PrismNotificationDefinition.PROGRAM_CORRECT_REQUEST) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_APPROVAL) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)  //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_WITHDRAWN) //
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withActionEnhancement(PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR),
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_WITHDRAWN) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST))));
    }

}
