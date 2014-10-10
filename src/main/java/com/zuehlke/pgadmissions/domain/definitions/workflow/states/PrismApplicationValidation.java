package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismApplicationValidation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_ASSESS_ELIGIBILITY) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotificationTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_TASK_REQUEST) //
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
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_VALIDATION_PENDING_COMPLETION) // 
                        .withTransitionAction(PrismAction.APPLICATION_COMPLETE_VALIDATION_STAGE) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_VALIDATION_PENDING_FEEDBACK) // 
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_ELIGIBILITY) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_ELIGIBILITY_ASSESSED_OUTCOME)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_COMMENT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
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
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_VALIDATION) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
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
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR)))); //
    
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
                        .withTransitionState(PrismState.APPLICATION_REJECTED_COMPLETED) // 
                        .withTransitionAction(PrismAction.APPLICATION_TERMINATE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROCESSED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_REJECTED_PENDING_EXPORT) // 
                        .withTransitionAction(PrismAction.APPLICATION_TERMINATE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROCESSED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(false)))))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR), //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_APPROVER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_VIEWER) // 
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROCESSED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROCESSED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(false))))));
    }

}
