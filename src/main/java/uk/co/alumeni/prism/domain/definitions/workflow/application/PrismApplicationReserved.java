package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_RESERVED_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_RESERVED_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationReserved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationCompleteReserved(state));
        stateActions.add(applicationSendMessageViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_RESERVED_PENDING_COMPLETION)); //
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditViewerRefereeViewerRecruiter(state)); //
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationCompleteReserved(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_RESERVED_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP);
    }

}
