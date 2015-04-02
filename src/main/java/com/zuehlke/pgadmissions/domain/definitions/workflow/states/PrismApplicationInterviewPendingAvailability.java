package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_POTENTIAL_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_POTENTIAL_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_INTERVIEW_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_WITHDRAW_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationViewEditReview;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationInterviewPendingAvailability extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReview()); //

		stateActions.add(applicationCompleteInterviewStagePendingAvailability());

		stateActions.add(applicationConfirmInterviewArrangements()); //

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationProvideInterviewAvailability() //
		        .withTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION //
		                .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP))); //

		stateActions.add(applicationUpdateInterviewAvailability(state));

		stateActions.add(applicationViewEditInterviewPendingAvailability(state)); //

		stateActions.add(applicationWithdrawInterviewPendingAvailability());
	}

	public static PrismStateAction applicationCompleteInterviewStagePendingAvailability() {
		return PrismApplicationInterview.applicationCompleteInterviewStage() //
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                        APPLICATION_DELETE_INTERVIEWEE_GROUP,
		                        APPLICATION_DELETE_INTERVIEWER_GROUP));
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
		                .withRoleTransitions(APPLICATION_DELETE_INTERVIEWEE_GROUP, //
		                        APPLICATION_DELETE_INTERVIEWER_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
		                        .withTransitionAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
		                        .withTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_DELETE_POTENTIAL_INTERVIEWEE_GROUP,
		                                APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP, //
		                                APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_INTERVIEW) //
		                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                        .withTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_DELETE_POTENTIAL_INTERVIEWEE_GROUP, //
		                                APPLICATION_DELETE_POTENTIAL_INTERVIEWER_GROUP));
	}

	public static PrismStateAction applicationProvideInterviewAvailability() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY) //
		        .withRaisesUrgentFlag()
		        .withNotification(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST) //
		        .withAssignments(APPLICATION_CONFIRMED_INTERVIEW_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION); //
	}

	public static PrismStateAction applicationUpdateInterviewAvailability(PrismState state) {
		return new PrismStateAction() //
		        .withAction(APPLICATION_UPDATE_INTERVIEW_AVAILABILITY) //
		        .withAssignments(APPLICATION_CONFIRMED_INTERVIEW_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(state) //
		                .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS)); //
	}

	public static PrismStateAction applicationViewEditInterviewPendingAvailability(PrismState state) {
		return applicationViewEditReview(state) //
		        .withAssignments(APPLICATION_POTENTIAL_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER)
		        .withAssignments(APPLICATION_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER);
	}

	public static PrismStateAction applicationWithdrawInterviewPendingAvailability() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_WITHDRAW) //
		        .withAssignments(APPLICATION_CREATOR) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_WITHDRAW_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                        APPLICATION_DELETE_INTERVIEWEE_GROUP, //
		                        APPLICATION_DELETE_INTERVIEWER_GROUP, //
		                        APPLICATION_DELETE_REFEREE_GROUP));
	}

}
