package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismEnhancementType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;

public class PrismApplicationInterviewPendingAvailability extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_COMMENT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
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
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
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
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
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
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
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
                        .withTransitionState(PrismState.APPLICATION_APPROVAL) // 
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_SUPERVISORS) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_EVALUATED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_APPROVED) // 
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_EVALUATED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW) // 
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_EVALUATED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_REJECTED) // 
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_REJECTION) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_EVALUATED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_REVIEW) // 
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_REVIEWERS) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_EVALUATED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(false)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
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
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWEE) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION), // 
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
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_INTERVIEW_SCHEDULED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withRestrictToOwner(false) //
                                .withMaximumPermitted(1), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_INTERVIEW_SCHEDULED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_INTERVIEW_SCHEDULED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(false)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REFEREE), // 
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
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING) // 
                        .withTransitionAction(PrismAction.APPLICATION_ESCALATE)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotificationTemplate(PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_INTERVIEW_RSVPED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(true), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(true))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_INTERVIEW_RSVPED_OUTCOME)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_PROVIDE_REFERENCE) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotificationTemplate(PrismNotificationTemplate.APPLICATION_PROVIDE_REFERENCE_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REFEREE))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.APPLICATION_CREATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
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
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(true)))))); //
    
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
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_PROCESSED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_REJECTED_PENDING_EXPORT) // 
                        .withTransitionAction(PrismAction.APPLICATION_TERMINATE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_PROCESSED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(false)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_ALL_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_VIEW_REFERENCE_DATA))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REFEREE), // 
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
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_REFERENCE_DATA) // 
                                .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE))), // 
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
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_REFERENCE_DATA) // 
                                .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE))), // 
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
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_REFERENCE_DATA) // 
                                .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE))), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withEnhancements(Arrays.asList( // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_EXPORT_DATA), // 
                            new PrismStateActionEnhancement() //
                                .withEnhancement(PrismEnhancementType.APPLICATION_EDIT_REFERENCE_DATA) // 
                                .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE))))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
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
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY) // 
                        .withTransitionAction(PrismAction.APPLICATION_VIEW_EDIT)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.APPLICATION_UPDATE_NOTIFICATION), // 
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
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_PROCESSED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(false))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_PROCESSED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.APPLICATION_CREATOR) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                                .withRestrictToOwner(false))))));
    }

}
