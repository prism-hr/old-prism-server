package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.*;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_REFERENCE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REFEREE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REFERENCE_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationEvaluation.APPLICATION_REFERENCED_TERMINATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_REFERENCE_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationReference extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //

        stateActions.add(applicationCompleteReference(state) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionAction(APPLICATION_VIEW_EDIT) //
                        .withRoleTransitions(APPLICATION_RETIRE_REFEREE_GROUP) //
                        .withStateTerminations(new PrismStateTermination() //
                                .withTerminationState(APPLICATION_REFERENCE)))); //

        stateActions.add(applicationSendMessageReference()); //
        stateActions.add(applicationEscalate(APPLICATION_REFERENCE_PENDING_COMPLETION));

        stateActions.add(applicationProvideReference() //
                .withStateTransitions(new PrismStateTransition() //
                                .withTransitionState(APPLICATION_REFERENCE) //
                                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
                                .withStateTransitionNotification(PrismRole.APPLICATION_REFEREE, PrismNotificationDefinition.APPLICATION_PROVIDE_REFERENCE_NOTIFICATION)
                                .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_REFERENCE_PENDING_COMPLETION) //
                                .withTransitionAction(APPLICATION_COMPLETE_REFERENCE_STAGE) //
                                .withStateTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
                                .withStateTransitionNotification(PrismRole.APPLICATION_REFEREE, PrismNotificationDefinition.APPLICATION_PROVIDE_REFERENCE_NOTIFICATION)
                                .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP),
                        new PrismStateTransition() //
                                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(APPLICATION_PROVIDED_REFERENCE_OUTCOME) //
                                .withStateTransitionNotification(PrismRole.APPLICATION_REFEREE, PrismNotificationDefinition.APPLICATION_PROVIDE_REFERENCE_NOTIFICATION)
                                .withRoleTransitions(APPLICATION_PROVIDE_REFERENCE_GROUP) //
                                .withStateTerminations(new PrismStateTermination() //
                                        .withTerminationState(APPLICATION_REFERENCE) //
                                        .withStateTerminationEvaluation(APPLICATION_REFERENCED_TERMINATION)))); //

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReference(state)); //
        stateActions.add(applicationWithdrawReference());
    }

    public static PrismStateAction applicationCompleteReference(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_REFERENCE_STAGE, state, //
                APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_RETIRE_REFEREE_GROUP);
    }

    public static PrismStateAction applicationProvideReference() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_REFERENCE) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(APPLICATION_PROVIDE_REFERENCE_REQUEST) //
                .withStateActionAssignments(APPLICATION_REFEREE);
    }

    public static PrismStateAction applicationSendMessageReference() {
        return applicationSendMessageViewerRecruiter() //
                .withStateActionAssignment(APPLICATION_REFEREE, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_REFEREE); //
    }

    public static PrismStateAction applicationViewEditReference(PrismState state) {
        return applicationViewEditWithViewerRecruiter(state) //
                .withStateActionAssignments(APPLICATION_REFEREE, APPLICATION_VIEW_AS_REFEREE);
    }

    public static PrismStateAction applicationWithdrawReference() {
        return applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                APPLICATION_TERMINATE_REFERENCE_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP);
    }

}
