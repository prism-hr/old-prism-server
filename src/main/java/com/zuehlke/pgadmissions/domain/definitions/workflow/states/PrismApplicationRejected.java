package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_REJECTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_APPROVER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_DELETE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_REJECTION_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproved.applicationEscalateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationApproved.applicationMoveToDifferentState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationValidation.applicationWithdrawValidation;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationCommentVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationEmailCreatorVerification;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.states.PrismApplicationVerification.applicationViewEditVerification;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;

public class PrismApplicationRejected extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentVerification()); //

		stateActions.add(new PrismStateAction() //
		        .withAction(APPLICATION_CONFIRM_REJECTION) //
		        .withRaisesUrgentFlag() //
		        .withNotification(SYSTEM_APPLICATION_TASK_REQUEST) //
		        .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
		        .withAssignments(PROGRAM_APPROVER) //
		        .withNotifications(APPLICATION_CREATOR, APPLICATION_CONFIRM_OFFER_RECOMMENDATION_NOTIFICATION) //
		        .withNotifications(APPLICATION_ADMINISTRATOR_GROUP, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withNotifications(PROGRAM_APPROVER, SYSTEM_APPLICATION_UPDATE_NOTIFICATION) //
		        .withTransitions(APPLICATION_CONFIRM_REJECTION_TRANSITION //
		                .withRoleTransitionsAndStateTerminations(Lists.newArrayList( //
		                        APPLICATION_DELETE_REFEREE_GROUP), //
		                        APPLICATION_TERMINATE_GROUP))); //

		stateActions.add(applicationEmailCreatorVerification()); //

		stateActions.add(applicationEscalateApproved()); //

		stateActions.add(applicationMoveToDifferentState());

		stateActions.add(applicationViewEditVerification(state)); //

		stateActions.add(applicationWithdrawValidation());
	}

}
