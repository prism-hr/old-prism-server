package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.BRANCH;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_COMPLETE_STATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition.APPLICATION_ASSIGN_INTERVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationCommentReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEmailCreatorReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationEscalateReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationReview.applicationWithdrawReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationViewEditVerification;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

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
		                        .withRoleTransitions(new PrismRoleTransition() //
		                                .withRole(PrismRole.APPLICATION_CREATOR) //
		                                .withTransitionType(BRANCH) //
		                                .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWEE), //
		                                new PrismRoleTransition() //
		                                        .withRole(APPLICATION_POTENTIAL_INTERVIEWER) //
		                                        .withTransitionType(CREATE) //
		                                        .withTransitionRole(APPLICATION_POTENTIAL_INTERVIEWER) //
		                                        .withPropertyDefinition(APPLICATION_ASSIGN_INTERVIEWER)), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
		                        .withTransitionAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
		                        .withTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_CREATE_INTERVIEWER_GROUP), //
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_INTERVIEW_PENDING_INTERVIEW) //
		                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                        .withTransitionEvaluation(APPLICATION_ASSIGNED_INTERVIEWER_OUTCOME) //
		                        .withRoleTransitions(new PrismRoleTransition() //
		                                .withRole(APPLICATION_CREATOR) //
		                                .withTransitionType(BRANCH) //
		                                .withTransitionRole(APPLICATION_INTERVIEWEE))
		                        .withRoleTransitions(APPLICATION_CREATE_INTERVIEWER_GROUP))); //

		stateActions.add(applicationCommentReview()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_COMPLETE_INTERVIEW_STAGE) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_COMPLETE_STATE_TRANSITION //
		                .withRoleTransitions(APPLICATION_DELETE_ADMINISTRATOR_GROUP))); //

		stateActions.add(applicationEmailCreatorReview()); //

		stateActions.add(applicationEscalateReview()); //

		stateActions.add(applicationViewEditVerification(state)); //

		stateActions.add(applicationWithdrawReview());
	}

}
