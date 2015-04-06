package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CORRECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.PROJECT_CORRECT_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVAL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectApproval.projectCompleteApprovalStage;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectApproval.projectEmailCreatorApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectApproval.projectEscalateApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectApproval.projectSuspendApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectApproval.projectTerminateApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectApproval.projectViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.project.PrismProjectApproval.projectWithdrawApproval;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismProjectApprovalPendingCorrection extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(projectCompleteApprovalStage());

		stateActions.add(new PrismStateAction() //
		        .withAction(PROJECT_CORRECT) //
		        .withRaisesUrgentFlag() //
		        .withNotification(PROJECT_CORRECT_REQUEST) //
		        .withAssignments(PROJECT_ADMINISTRATOR) //
		        .withTransitions(new PrismStateTransition() //
		                .withTransitionState(PROJECT_APPROVAL) //
		                .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST))); //

		stateActions.add(projectEmailCreatorApproval()); //

		stateActions.add(projectEscalateApproval()); //

		stateActions.add(projectSuspendApproval()); //

		stateActions.add(projectTerminateApproval()); //

		stateActions.add(projectViewEditApproval()); //

		stateActions.add(projectWithdrawApproval());
	}

}
