package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismProjectApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_COMPLETE_APPROVAL_STAGE) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotificationTemplate(PrismNotificationDefinition.SYSTEM_PROJECT_TASK_REQUEST) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR))) //
                .withNotifications(Arrays.asList( //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION),
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION),
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVED) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_REJECTED) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_APPROVED_OUTCOME)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_REJECTED) //
                        .withTransitionAction(PrismAction.PROJECT_ESCALATE)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_SUSPEND) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_REACTIVATION) //
                        .withTransitionAction(PrismAction.PROJECT_SUSPEND)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_TERMINATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withNotifications(Arrays.asList( //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_WITHDRAWN) //
                        .withTransitionAction(PrismAction.PROJECT_TERMINATE)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withActionEnhancement(PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR),
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_WITHDRAWN) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST))));
    }

}
