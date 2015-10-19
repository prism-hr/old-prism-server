package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_APPOINTMENT_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_APPROVAL_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationCompleteApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationProvideHiringManagerApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationTerminateApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationWithdrawApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovalPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationCompleteApproval(state)); //

        stateActions.add(applicationProvideHiringManagerApproval() //
                .withTransitions(APPLICATION_PROVIDE_HIRING_MANAGER_APPROVAL_TRANSITION //
                        .withRoleTransitions(APPLICATION_CONFIRM_APPOINTMENT_GROUP)));

        stateActions.add(applicationTerminateApproval());
        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_APPROVAL_PENDING_COMPLETION)); //
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditApproval(state)); //
        stateActions.add(applicationWithdrawApproval());
    }

}
