package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_REFERENCE_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReference.applicationCompleteReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReference.applicationProvideReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReference.applicationViewEditReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReference.applicationWithdrawReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReferencePendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //

        stateActions.add(applicationCompleteReference(state) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_COMPLETE_REFERENCE_STAGE_REQUEST));

        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(applicationProvideReference()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
                        .withTransitionAction(APPLICATION_COMPLETE_REFERENCE_STAGE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP))); //

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReference(state)); //
        stateActions.add(applicationWithdrawReference());
    }

}
