package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_REFERENCE_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_REVIVE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationEvaluation.APPLICATION_REFERENCED_TERMINATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REFERENCE_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationWorkflow.applicationWithdraw;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTermination;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismApplicationReference extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiter()); //
		stateActions.add(applicationCompleteReference()); //
		stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
		stateActions.add(applicationEscalate(APPLICATION_REFERENCE_PENDING_COMPLETION));

		stateActions.add(applicationProvideReference() //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(APPLICATION_REFERENCE) //
		                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                .withTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
		                .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
		                        .withTransitionAction(APPLICATION_COMPLETE_STAGE) //
		                        .withTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP),
		                new PrismStateTransition() //
		                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
		                        .withTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
		                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP) //
		                        .withStateTerminations(new PrismStateTermination() //
		                                .withTerminationState(APPLICATION_REFERENCE) //
		                                .withStateTerminationEvaluation(APPLICATION_REFERENCED_TERMINATION)))); //

		stateActions.add(applicationViewEditReference(state)); //
		stateActions.add(applicationWithdrawReference());
	}

	public static PrismStateAction applicationCompleteReference() {
		return applicationCompleteState(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_REVIVE_REFEREE_GROUP);
	}

	public static PrismStateAction applicationProvideReference() {
		return new PrismStateAction() //
		        .withAction(APPLICATION_PROVIDE_REFERENCE) //
		        .withRaisesUrgentFlag() //
		        .withNotification(APPLICATION_PROVIDE_REFERENCE_REQUEST) //
		        .withAssignments(APPLICATION_REFEREE) //
		        .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(APPLICATION_CREATOR, SYSTEM_APPLICATION_UPDATE_NOTIFICATION);
	}

	public static PrismStateAction applicationViewEditReference(PrismState state) {
		return PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter(state) //
		        .withAssignments(APPLICATION_REFEREE, APPLICATION_VIEW_AS_REFEREE);
	}

	public static PrismStateAction applicationWithdrawReference() {
		return applicationWithdraw(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_DELETE_REFEREE_GROUP);
	}

}
