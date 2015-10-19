package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_APPOINTMENT_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationCompleteApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationProvideHiringManagerApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationTerminateApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationWithdrawApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovalPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //

        stateActions.add(applicationCompleteApproval(state) //
                .withRaisesUrgentFlag()); //

        stateActions.add(applicationProvideHiringManagerApproval() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_COMPLETE_APPROVAL_STAGE) //
                        .withRoleTransitions(APPLICATION_CONFIRM_APPOINTMENT_GROUP))); //

        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_HIRING_MANAGER_GROUP));

        stateActions.add(applicationTerminateApproval());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditApproval(state)); //
        stateActions.add(applicationWithdrawApproval());
    }

}
