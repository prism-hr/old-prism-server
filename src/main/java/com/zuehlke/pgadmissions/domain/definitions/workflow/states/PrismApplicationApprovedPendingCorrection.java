package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismEnhancementType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationApprovedPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_COMMENT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SECONDARY_SUPERVISOR), // 
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
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_APPROVED_PENDING_CORRECTION) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_CORRECT) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotificationTemplate(PrismNotificationTemplate.APPLICATION_TASK_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_APPROVED_PENDING_EXPORT) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SECONDARY_SUPERVISOR), // 
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
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_APPROVED_COMPLETED) // 
                        .withTransitionAction(PrismAction.APPLICATION_ESCALATE)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_ALL_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_PRIMARY_SUPERVISOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SECONDARY_SUPERVISOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_ALL_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_ALL_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_APPROVER) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_VIEWER) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_APPROVED_PENDING_EXPORT) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST))));
    }

}
