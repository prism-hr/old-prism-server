package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_SUPERVISION_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationCompleteApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationConfirmPrimarySupervision;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationConfirmSecondarySupervision;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationWithdrawApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovalPendingFeedback extends PrismWorkflowState {

	@Override
	protected void setStateActions() {
		stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //

		stateActions.add(applicationCompleteApproval(state)); //

		stateActions.add(applicationConfirmPrimarySupervision() //
		        .withTransitions(APPLICATION_CONFIRM_SUPERVISION_TRANSITION //
		                .withRoleTransitions(APPLICATION_CONFIRM_PRIMARY_SUPERVISION_GROUP)));

		stateActions.add(applicationConfirmSecondarySupervision() //
		        .withTransitions(APPLICATION_CONFIRM_SUPERVISION_TRANSITION //
		                .withRoleTransitions(APPLICATION_CONFIRM_SECONDARY_SUPERVISION_GROUP)));

		stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //
		stateActions.add(applicationEscalate(APPLICATION_APPROVAL_PENDING_COMPLETION)); //
        stateActions.add(applicationUploadReference(state));
		stateActions.add(applicationViewEditApproval(state)); //
		stateActions.add(applicationWithdrawApproval());
	}

}
