package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismProjectDisabledPendingReactivation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_CONCLUDE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) // //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE)), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)))); //
        
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
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(true) //
            .withActionEnhancement(PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER) //
            .withNotificationTemplate(PrismNotificationDefinition.SYSTEM_PROJECT_TASK_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR))) //
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
                        .withTransitionState(PrismState.PROJECT_APPROVED) // 
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_VIEW_EDIT_OUTCOME)
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
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_VIEW_EDIT_OUTCOME)
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
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_VIEW_EDIT_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_VIEW_EDIT_OUTCOME)
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
                                .withRestrictToOwner(false)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) // //
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
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROJECT_ESCALATE)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_SUSPEND) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) // //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROJECT_SUSPEND)))); //
    }

}
