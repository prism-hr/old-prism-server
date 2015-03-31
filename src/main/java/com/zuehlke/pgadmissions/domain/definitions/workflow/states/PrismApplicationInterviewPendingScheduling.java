package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationCompleteInterviewStagePendingAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationUpdateInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationViewEditInterviewPendingAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationWithdrawInterviewPendingAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;

import java.util.Arrays;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
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
		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationCompleteInterviewStagePendingAvailability()); //

		stateActions.add(PrismApplicationInterviewPendingAvailability.applicationConfirmInterviewArrangements() //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST)); //

		stateActions.add(applicationEmailCreatorReview()); //

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

		stateActions.add(applicationUpdateInterviewAvailability(state)); //

		stateActions.add(applicationViewEditInterviewPendingAvailability(state)); //

		stateActions.add(applicationWithdrawInterviewPendingAvailability());
	}

}
