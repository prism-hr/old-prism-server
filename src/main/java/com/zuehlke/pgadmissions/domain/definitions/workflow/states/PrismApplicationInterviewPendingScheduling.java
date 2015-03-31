package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.google.common.collect.Lists;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTermination;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismApplicationInterviewPendingScheduling extends PrismWorkflowState {

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
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                        .withTransitionAction(PrismAction.APPLICATION_COMMENT)))); //

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
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_APPROVAL) //
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_SUPERVISORS) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_APPROVED) //
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_OFFER_RECOMMENDATION) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW) //
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_REFERENCE) //
                        .withTransitionAction(PrismAction.APPLICATION_PROVIDE_REFERENCE) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_REJECTED) //
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_REJECTION) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false))), //
                        new PrismStateTransition() //
	                        .withTransitionState(PrismState.APPLICATION_RESERVED) //
	                        .withTransitionAction(PrismAction.APPLICATION_RESERVE) //
	                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
	                        .withRoleTransitions(Arrays.asList( //
	                            new PrismRoleTransition() //
	                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
	                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
	                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
	                                .withRestrictToOwner(false), //
	                            new PrismRoleTransition() //
	                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
	                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
	                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
	                                .withRestrictToOwner(false), //
	                            new PrismRoleTransition() //
	                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
	                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
	                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
	                                .withRestrictToOwner(false), //
	                            new PrismRoleTransition() //
	                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
	                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
	                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
	                                .withRestrictToOwner(false), //
	                            new PrismRoleTransition() //
	                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
	                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
	                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
	                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_REVIEW) //
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_REVIEWERS) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false))),
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_VERIFICATION) //
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_ELIGIBILITY) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_COMPLETED_STATE_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false)))))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotification(PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST) //
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
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW) //
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
                        .withTransitionAction(PrismAction.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
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
                .withNotifications(Arrays.asList( //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.APPLICATION_CREATOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_REJECTED_COMPLETED) //
                        .withTransitionAction(PrismAction.APPLICATION_ESCALATE) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_ESCALATED_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
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
                                .withRestrictToOwner(false))) //
                        .withStateTerminations(Lists.newArrayList( //
                            new PrismStateTermination().withTerminationState(PrismState.APPLICATION_REFERENCE), //
                            new PrismStateTermination().withTerminationState(PrismState.APPLICATION_VERIFICATION))), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_REJECTED_PENDING_EXPORT) //
                        .withTransitionAction(PrismAction.APPLICATION_ESCALATE) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_ESCALATED_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
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
                                .withRestrictToOwner(false))) //
                        .withStateTerminations(Lists.newArrayList( //
                            new PrismStateTermination().withTerminationState(PrismState.APPLICATION_REFERENCE), //
                            new PrismStateTermination().withTerminationState(PrismState.APPLICATION_VERIFICATION)))))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotification(PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER))) //
                .withNotifications(Arrays.asList( //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(true), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withRestrictToOwner(true)))))); //

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
                        .withDefinition(PrismNotificationDefinition.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                        .withTransitionAction(PrismAction.APPLICATION_VIEW_EDIT) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_REFEREE) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_REFEREE) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.APPLICATION_REFEREE) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR) //
                                .withRestrictToOwner(false))))) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_CREATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_REFEREE) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE)
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER) //
                        .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMITTER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER) //
                        .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_RECRUITER) //
                        .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_APPROVER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_VIEWER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_RECRUITER) //
                        .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_RECRUITER) //
                        .withDelegatedAction(PrismAction.APPLICATION_PROVIDE_REFERENCE)))); //

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
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_WITHDRAWN_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
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
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_WITHDRAWN_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_INTERVIEWEE) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_INTERVIEWER) //
                                .withTransitionType(PrismRoleTransitionType.UPDATE) //
                                .withTransitionRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                                .withRestrictToOwner(false), //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
                                .withTransitionType(PrismRoleTransitionType.RETIRE) //
                                .withTransitionRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE) //
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
