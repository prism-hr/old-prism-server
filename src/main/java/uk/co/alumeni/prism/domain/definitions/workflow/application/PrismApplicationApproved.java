package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_APPOINTEE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_CREATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_OFFER_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationComment()); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_OFFER) //
                .withRaisesUrgentFlag() //
                .withStateActionAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withStateTransitions(APPLICATION_CONFIRM_OFFER_TRANSITION)); //

        stateActions.add(applicationSendMessageApproved());
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP)); //
        stateActions.add(applicationCompleteApproved(state));
        stateActions.add(applicationUploadReference(state));
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit(state)); //
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_APPROVER_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationCompleteApproved(PrismState state) {
        return applicationCompleteApproved(state, false);
    }

    public static PrismStateAction applicationCompleteApprovedAppointeeHiringManager(PrismState state) {
        return applicationCompleteApproved(state, true);
    }

    public static PrismStateAction applicationConfirmOfferAcceptance(PrismNotificationDefinition notificationDefinition) {
        return new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_OFFER_ACCEPTANCE) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(notificationDefinition)
                .withStateActionAssignments(APPLICATION_APPOINTEE) //
                .withStateTransitions(APPLICATION_CONFIRM_OFFER_ACCEPTANCE_TRANSITION);
    }

    public static PrismStateAction applicationSendMessageApproved() {
        return PrismApplicationWorkflow.applicationSendMessage() //
                .withStateActionAssignment(APPLICATION_CREATOR, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_CREATOR);
    }

    private static PrismStateAction applicationCompleteApproved(PrismState state, boolean retireAppointee) {
        PrismStateAction stateAction = applicationCompleteState(APPLICATION_COMPLETE_APPROVED_STAGE, state, APPLICATION_PARENT_APPROVER_GROUP);

        if (retireAppointee) {
            stateAction.getStateTransitions().forEach(
                    transition -> transition.withRoleTransitions(APPLICATION_RETIRE_HIRING_MANAGER_GROUP, APPLICATION_RETIRE_APPOINTEE_GROUP));
        }

        return stateAction;
    }

}
