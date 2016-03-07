package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_PARTNER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PARTNERSHIP_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_APPOINTEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationSendMessageViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovedPendingPartnerAcceptance extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_PARTNER_APPROVAL) //
                .withRaisesUrgentFlag() //
                .withPartnerAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_APPROVED)
                        .withTransitionAction(APPLICATION_CONFIRM_OFFER) //
                        .withStateTransitionEvaluation(APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE) //
                                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME) //
                                .withRoleTransitions(APPLICATION_CREATE_APPOINTEE_GROUP)));

        stateActions.add(applicationSendMessageViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_APPROVED_COMPLETED)); //
        stateActions.add(applicationViewEdit()); //
    }

}
