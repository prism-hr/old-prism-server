package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_VERIFICATION_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_ELIGIBILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_VIEWER_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VERIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VERIFICATION_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationCommentValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationCompleteValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationEmailCreatorValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationViewEditValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationWithdrawValidation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTermination;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationVerification extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(applicationCompleteVerification()); //

		stateActions.add(applicationConfirmEligibility() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_VERIFICATION_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_COMPLETE_VERIFICATION_STAGE) //
		                .withTransitionEvaluation(APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME), //
		                new PrismStateTransition() //
		                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                        .withTransitionEvaluation(APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME) //
		                        .withStateTerminations(new PrismStateTermination().withTerminationState(APPLICATION_VERIFICATION)))); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_ESCALATE) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_VERIFICATION_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_ESCALATE))); //

		stateActions.add(applicationViewEditVerification(state)); //

		stateActions.add(applicationWithdrawValidation());
	}

	public static PrismStateAction applicationCommentVerification() {
		return applicationCommentValidation() //
		        .withAssignments(APPLICATION_VIEWER_RECRUITER);
	}

	public static PrismStateAction applicationCompleteVerification() {
		return applicationCompleteValidation();
	}

	public static PrismStateAction applicationConfirmEligibility() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_CONFIRM_ELIGIBILITY) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(INSTITUTION_ADMITTER) //
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION)
		        .withNotifications(INSTITUTION_ADMITTER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
	}

	public static PrismStateAction applicationEmailCreatorVerification() {
		return applicationEmailCreatorValidation() //
		        .withAssignments(APPLICATION_VIEWER_RECRUITER);
	}

	public static PrismStateAction applicationViewEditVerification(PrismState state) {
		return applicationViewEditValidation(state) //
		        .withAssignments(APPLICATION_VIEWER_RECRUITER, APPLICATION_VIEW_AS_RECRUITER);
	}

}
