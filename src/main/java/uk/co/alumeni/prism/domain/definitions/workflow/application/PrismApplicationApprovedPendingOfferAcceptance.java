package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_OFFER_ACCEPTANCE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.applicationCompleteApprovedAppointeeHiringManager;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.applicationConfirmOfferAcceptance;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovedPendingOfferAcceptance extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationConfirmOfferAcceptance(APPLICATION_CONFIRM_OFFER_ACCEPTANCE_REQUEST));
        stateActions.add(applicationCommentViewerRecruiter()); //
        stateActions.add(applicationCompleteApprovedAppointeeHiringManager(state)); //
        stateActions.add(applicationEmailCreatorViewerRecruiter()); //
        stateActions.add(applicationViewEdit()); //
    }

}
