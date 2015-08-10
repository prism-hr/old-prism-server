package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REFERENCE_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReference.applicationCompleteReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReference.applicationProvideReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReference.applicationTerminateReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReference.applicationViewEditReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReference.applicationWithdrawReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReferencePendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //

        stateActions.add(applicationCompleteReference(state) //
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_APPLICATION_TASK_REQUEST));

        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(applicationProvideReference()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
                        .withTransitionAction(APPLICATION_COMPLETE_REFERENCE_STAGE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP))); //

        stateActions.add(applicationTerminateReference());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReference(state)); //
        stateActions.add(applicationWithdrawReference());
    }

}
