package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_APPROVAL_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_APPOINTMENT_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_HIRING_MANAGER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationCompleteApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationConfirmAppointment;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationTerminateApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationViewEditApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationApproval.applicationWithdrawApproval;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovalPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator()); //

        stateActions.add(applicationCompleteApproval(state) //
                .withRaisesUrgentFlag()); //

        stateActions.add(applicationConfirmAppointment() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_COMPLETE_APPROVAL_STAGE) //
                        .withRoleTransitions(APPLICATION_CONFIRM_APPOINTMENT_GROUP))); //

        stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator()); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_HIRING_MANAGER_GROUP));

        stateActions.add(applicationTerminateApproval());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditApproval(state)); //
        stateActions.add(applicationWithdrawApproval());
    }

}
