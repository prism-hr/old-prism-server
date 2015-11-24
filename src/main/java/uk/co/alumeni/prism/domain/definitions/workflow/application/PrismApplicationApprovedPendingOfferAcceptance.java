package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;

import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismApplicationApprovedPendingOfferAcceptance extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_CONFIRM_OFFER_ACCEPTANCE) //
                .withRaisesUrgentFlag() //
                .withNotification(PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_REQUEST)
                .withAssignments(PrismRole.APPLICATION_APPOINTEE) //
                .withNotifications(PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP, PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_NOTIFICATION) //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_RETIRE_APPOINTEE_GROUP)));

        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
    }

}
