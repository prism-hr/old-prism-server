package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_MESSAGING_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationMessagingPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerReferee());

        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_MESSAGING_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withRaisesUrgentFlag());

        stateActions.add(applicationSendMessageViewerReferee());
        stateActions.add(applicationEscalate());
        stateActions.add(applicationViewEditViewerReferee(state));
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP));
    }

}
