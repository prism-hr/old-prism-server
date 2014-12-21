package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismProjectApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_CONCLUDE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVED) //
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_DEACTIVATED) //
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) //
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)))); //

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
            .withAction(PrismAction.PROJECT_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withActionEnhancement(PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_APPROVED) //
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_VIEW_EDIT_OUTCOME)
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_DEACTIVATED) //
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_VIEW_EDIT_OUTCOME)
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) //
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_VIEW_EDIT_OUTCOME)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_CREATE_APPLICATION) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_UNSUBMITTED) //
                        .withTransitionAction(PrismAction.APPLICATION_COMPLETE) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_CREATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(true) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.BRANCH) //
                                .withTransitionRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.BRANCH) //
                                .withTransitionRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withRestrictToOwner(false)))))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) //
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
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION)))
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) //
                        .withTransitionAction(PrismAction.PROJECT_TERMINATE)))); //
    }

}
