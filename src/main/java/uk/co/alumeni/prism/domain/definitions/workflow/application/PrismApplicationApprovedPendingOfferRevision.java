package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_REVISE_OFFER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_REVISE_OFFER_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_APPROVER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_REVISE_OFFER_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.applicationCompleteApprovedWithAppointee;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovedPendingOfferRevision extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationCompleteApprovedWithAppointee(state));

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_REVISE_OFFER) //
                .withRaisesUrgentFlag() //
                .withStateActionAssignments(APPLICATION_PARENT_APPROVER_GROUP) //
                .withStateTransitions(APPLICATION_REVISE_OFFER_TRANSITION) //
                .withNotification(APPLICATION_REVISE_OFFER_REQUEST));

        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationViewEdit()); //
    }

}
