package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismApplicationUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_COMPLETE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_CREATOR)
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR))) //
                .withNotifications(Arrays.asList( //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.APPLICATION_CREATOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_COMPLETE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_VALIDATION) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_REFEREE) //
                                .withRestrictToOwner(false) //
                                .withPropertyDefinition(PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withRestrictToOwner(false) //
                                .withPropertyDefinition(PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR))),
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_REFEREE) //
                                .withRestrictToOwner(false) //
                                .withPropertyDefinition(PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_REFEREE), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withRestrictToOwner(false) //
                                .withPropertyDefinition(PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR)))))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_UNSUBMITTED_PENDING_COMPLETION) //
                        .withTransitionAction(PrismAction.APPLICATION_ESCALATE)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_CREATOR))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED_UNSUBMITTED) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST))));
    }

}
