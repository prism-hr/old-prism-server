package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_WITHDRAW_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationUpdateInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationViewEditReview;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationInterviewPendingInterview extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentReview()); //

		stateActions.add(PrismApplicationInterview.applicationCompleteInterviewStage() //
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                        APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP, //
		                        APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP)));

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
		                .withTransitionAction(APPLICATION_ESCALATE) //
		                .withRoleTransitions(APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP))); //

		stateActions.add(applicationUpdateInterviewAvailability(APPLICATION_INTERVIEW_PENDING_SCHEDULING)); //

		stateActions.add(applicationViewEditPendingInterview(state)); //

		stateActions.add(PrismApplicationReview.applicationWithdrawReview() //
		        .withTransitions(APPLICATION_WITHDRAW_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                        APPLICATION_DELETE_CONFIRMED_INTERVIEWEE_GROUP, //
		                        APPLICATION_DELETE_CONFIRMED_INTERVIEWER_GROUP, //
		                        APPLICATION_DELETE_REFEREE_GROUP)));
	}

	public static PrismStateAction applicationViewEditPendingInterview(PrismState state) {
		return applicationViewEditReview(state) //
		        .withAssignments(APPLICATION_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER);
	}

}
