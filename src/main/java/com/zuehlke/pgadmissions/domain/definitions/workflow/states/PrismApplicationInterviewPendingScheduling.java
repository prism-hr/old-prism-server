package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_TERMINATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ESCALATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationCompleteInterviewStagePendingAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationProvideInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationUpdateInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationViewEditInterviewPendingAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationInterviewPendingAvailability.applicationWithdrawInterviewPendingAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

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
		        .withAction(APPLICATION_ESCALATE) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_TERMINATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_ESCALATE_TRANSITION //
		                .withRoleTransitionsAndStateTerminations(Lists.newArrayList( //
		                        APPLICATION_DELETE_ADMINISTRATOR_GROUP, //
		                        APPLICATION_DELETE_REFEREE_GROUP, //
		                        APPLICATION_DELETE_INTERVIEWEE_GROUP, //
		                        APPLICATION_DELETE_INTERVIEWER_GROUP), //
		                        APPLICATION_TERMINATE_GROUP)));

		stateActions.add(applicationProvideInterviewAvailability() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(state) //
		                .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
		                .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP)));

		stateActions.add(applicationUpdateInterviewAvailability(state)); //

		stateActions.add(applicationViewEditInterviewPendingAvailability(state)); //

		stateActions.add(applicationWithdrawInterviewPendingAvailability());
	}
}
