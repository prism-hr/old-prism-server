package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;

public class PrismProjectDisabledPendingReactivation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_CONCLUDE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE)), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_CONFIGURE) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(true) //
            .withPostComment(true) //
            .withNotificationTemplate(PrismNotificationTemplate.PROJECT_TASK_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_APPROVED) // 
                        .withTransitionAction(PrismAction.PROJECT_CONFIGURE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_CONFIGURED_OUTCOME)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
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
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
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
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DEACTIVATED) // 
                        .withTransitionAction(PrismAction.PROJECT_CONFIGURE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_CONFIGURED_OUTCOME)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
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
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
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
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED) // 
                        .withTransitionAction(PrismAction.PROJECT_CONFIGURE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_CONFIGURED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROJECT_CONFIGURE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_CONFIGURED_OUTCOME)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
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
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
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
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                                .withRestrictToOwner(false)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROJECT_ESCALATE)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_SUSPEND) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROJECT_SUSPEND)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_VIEW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_APPROVER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_VIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_VIEW_APPLICATION_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_APPROVER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_VIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR))));
    }

}
