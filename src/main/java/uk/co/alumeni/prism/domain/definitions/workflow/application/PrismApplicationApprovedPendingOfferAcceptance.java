package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER_ACCEPTANCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_APPOINTEE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.applicationCompleteApprovedWithAppointee;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovedPendingOfferAcceptance extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_OFFER_ACCEPTANCE) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_CONFIRM_OFFER_ACCEPTANCE_REQUEST)
                .withAssignments(APPLICATION_APPOINTEE) //
                .withNotifications(APPLICATION_PARENT_APPROVER_GROUP, APPLICATION_CONFIRM_OFFER_ACCEPTANCE_NOTIFICATION) //
                .withStateTransitions(APPLICATION_CONFIRM_OFFER_ACCEPTANCE_TRANSITION));

        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationCompleteApprovedWithAppointee(state)); //
        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationViewEdit()); //
    }

}
