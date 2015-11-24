package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismApplicationApprovedPendingPartnerAcceptance extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_PROVIDE_PARTNER_APPROVAL) //
                .withRaisesUrgentFlag() //
                .withPartnerAssignments(PrismRole.DEPARTMENT_ADMINISTRATOR, PrismRole.DEPARTMENT_APPROVER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_APPROVED)
                        .withTransitionAction(PrismAction.APPLICATION_CONFIRM_OFFER) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE) //
                                .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME) //
                                .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CREATE_APPOINTEE_GROUP)));

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_APPROVED_COMPLETED)); //
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
    }

}
