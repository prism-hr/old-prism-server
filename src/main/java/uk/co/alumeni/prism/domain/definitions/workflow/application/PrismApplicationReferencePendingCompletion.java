package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_REFERENCE_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReference.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationReferencePendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //

        stateActions.add(applicationCompleteReference(state) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(APPLICATION_COMPLETE_REFERENCE_STAGE_REQUEST));

        stateActions.add(applicationSendMessageReference()); //
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(applicationProvideReference()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
                        .withTransitionAction(APPLICATION_COMPLETE_REFERENCE_STAGE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP)
                        .withStateTransitionNotification(PrismRole.APPLICATION_REFEREE, PrismNotificationDefinition.APPLICATION_PROVIDE_REFERENCE_NOTIFICATION))); //

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReference(state)); //
        stateActions.add(applicationWithdrawReference());
    }

}
