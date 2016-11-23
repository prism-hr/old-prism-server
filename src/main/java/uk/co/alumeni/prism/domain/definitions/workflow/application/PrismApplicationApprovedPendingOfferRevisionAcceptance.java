package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_REVISED_OFFER_ACCEPTANCE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.*;

public class PrismApplicationApprovedPendingOfferRevisionAcceptance extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationConfirmOfferAcceptance(APPLICATION_CONFIRM_REVISED_OFFER_ACCEPTANCE_REQUEST));
        stateActions.add(PrismApplicationWorkflow.applicationComment()); //
        stateActions.add(applicationCompleteApprovedAppointeeHiringManager(state)); //
        stateActions.add(applicationSendMessageApproved()); //
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
    }

}
