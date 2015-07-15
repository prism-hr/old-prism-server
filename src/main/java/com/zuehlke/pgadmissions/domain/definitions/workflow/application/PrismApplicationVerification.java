package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_VERIFICATION_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_ELIGIBILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMITTER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VERIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_VERIFICATION_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTermination;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationVerification extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter());
		
		stateActions.add(applicationCompleteVerification(state) //
				.withTransitions(new PrismStateTransition() //
				.withTransitionAction(APPLICATION_VIEW_EDIT) //
                .withStateTerminations(new PrismStateTermination() //
                        .withTerminationState(APPLICATION_VERIFICATION))));

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_CONFIRM_ELIGIBILITY) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(INSTITUTION_ADMITTER) //
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION)
		        .withNotifications(INSTITUTION_ADMITTER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_VERIFICATION_PENDING_COMPLETION) //
		                .withTransitionAction(APPLICATION_COMPLETE_VERIFICATION_STAGE) //
		                .withTransitionEvaluation(APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME), //
		                new PrismStateTransition() //
		                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                        .withTransitionEvaluation(APPLICATION_CONFIRMED_ELIGIBILITY_OUTCOME) //
		                        .withStateTerminations(new PrismStateTermination() //
		                                .withTerminationState(APPLICATION_VERIFICATION))));

		stateActions.add(applicationEmailCreatorWithViewerRecruiter());
		stateActions.add(applicationEscalate(APPLICATION_VERIFICATION_PENDING_COMPLETION));
        stateActions.add(applicationUploadReference(state));
		stateActions.add(applicationViewEditWithViewerRecruiter(state));
		stateActions.add(applicationWithdrawVerification());
	}

	public static PrismStateAction applicationWithdrawVerification() {
		return applicationWithdraw(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_RETIRE_REFEREE_GROUP);
	}

	public static PrismStateAction applicationCompleteVerification(PrismState state) {
		return applicationCompleteState(APPLICATION_COMPLETE_VERIFICATION_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP);
	}

}
