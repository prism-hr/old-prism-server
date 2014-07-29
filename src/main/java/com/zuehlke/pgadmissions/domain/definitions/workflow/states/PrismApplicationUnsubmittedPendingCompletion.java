package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

public class PrismApplicationUnsubmittedPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_COMPLETE) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(true) //
            .withNotificationTemplate(PrismNotificationTemplate.APPLICATION_COMPLETE_REQUEST) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_CREATOR)
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.APPLICATION_CREATOR) //
                        .withTemplate(PrismNotificationTemplate.APPLICATION_COMPLETE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_VALIDATION) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_REFEREE) //
                                .withRestrictToOwner(false) //
                                .withMinimumPermitted(3) //
                                .withMaximumPermitted(3), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withRestrictToOwner(false)))))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_TERMINATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.APPLICATION_CREATOR) //
                        .withTemplate(PrismNotificationTemplate.APPLICATION_TERMINATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED) //
                        .withTransitionAction(PrismAction.APPLICATION_TERMINATE)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_CREATOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST))));
    }

}
