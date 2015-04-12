package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_POTENTIAL_INTERVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_POTENTIAL_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_POTENTIAL_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_POTENTIAL_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_INTERVIEW_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterview extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ASSIGN_INTERVIEWERS) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_INTERVIEW) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
		                .withRoleTransitions(APPLICATION_CREATE_ADMINISTRATOR_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_AVAILABILITY) //
		                        .withTransitionAction(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY) //
		                        .withTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_CREATE_POTENTIAL_INTERVIEWEE_GROUP, //
		                                APPLICATION_CREATE_POTENTIAL_INTERVIEWER_GROUP), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
		                        .withTransitionAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
		                        .withTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_CREATE_INTERVIEWER_GROUP), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_INTERVIEW) //
		                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                        .withTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_CREATE_INTERVIEWEE_GROUP, //
		                                APPLICATION_CREATE_INTERVIEWER_GROUP))); //

		stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, APPLICATION_RETIRE_ADMINISTRATOR_GROUP)); //
		stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, APPLICATION_ADMINISTRATOR_GROUP,
		        APPLICATION_RETIRE_ADMINISTRATOR_GROUP));
		stateActions.add(applicationViewEditWithViewerRecruiterAndAdministrator(state)); //
		stateActions.add(applicationWithdraw(APPLICATION_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_REFEREE_GROUP, //
		        APPLICATION_RETIRE_ADMINISTRATOR_GROUP));
	}

	public static PrismStateAction applicationCompleteInterviewScheduling(PrismState state) {
		return applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, //
		        APPLICATION_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_INTERVIEWEE_GROUP,
		        APPLICATION_RETIRE_INTERVIEWER_GROUP);
	}

	public static PrismStateAction applicationCompleteInterviewScheduled(PrismState state) {
		return applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, //
		        APPLICATION_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP);
	}

	public static PrismStateAction applicationConfirmInterviewArrangements() {
		return new PrismStateAction() //
		        .withAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(APPLICATION_CONFIRMED_INTERVIEW_GROUP, APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_INTERVIEW) //
		                .withTransitionAction(APPLICATION_ASSIGN_INTERVIEWERS) //
		                .withTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
		                .withRoleTransitions(APPLICATION_RETIRE_INTERVIEWEE_GROUP, //
		                        APPLICATION_RETIRE_INTERVIEWER_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
		                        .withTransitionAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
		                        .withTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP, //
		                                APPLICATION_RETIRE_POTENTIAL_INTERVIEWER_GROUP, //
		                                APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_INTERVIEW) //
		                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                        .withTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP, //
		                                APPLICATION_RETIRE_POTENTIAL_INTERVIEWER_GROUP));
	}

	public static PrismStateAction applicationProvideInterviewAvailability() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY) //
		        .withRaisesUrgentFlag()
		        .withNotification(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST) //
		        .withAssignments(APPLICATION_POTENTIAL_INTERVIEW_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION); //
	}

	public static PrismStateAction applicationProvideInterviewFeedback() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_INTERVIEWER) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
	}

	public static PrismStateAction applicationUpdateInterviewAvailability() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_UPDATE_INTERVIEW_AVAILABILITY) //
		        .withAssignments(APPLICATION_CONFIRMED_INTERVIEW_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION); //
	}

	public static PrismStateAction applicationViewEditInterviewScheduling(PrismState state) {
		return applicationViewEditInterviewScheduled(state) //
		        .withAssignments(APPLICATION_POTENTIAL_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER);
	}

	public static PrismStateAction applicationViewEditInterviewScheduled(PrismState state) {
		return applicationViewEditWithViewerRecruiterAndAdministrator(state) //
		        .withAssignments(APPLICATION_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER);
	}

	public static PrismStateAction applicationWithdrawInterviewScheduling() {
		return applicationWithdraw(APPLICATION_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_REFEREE_GROUP,
		        APPLICATION_RETIRE_INTERVIEWEE_GROUP,
		        APPLICATION_RETIRE_INTERVIEWER_GROUP);
	}

	public static PrismStateAction applicationWithdrawInterviewScheduled() {
		return applicationWithdraw(APPLICATION_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
		        APPLICATION_RETIRE_REFEREE_GROUP,
		        APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP);
	}

}
