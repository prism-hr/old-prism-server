package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_OFFER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_PARTNER_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_APPROVER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.INSTITUTION_APPROVER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_APPOINTEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.applicationCompleteApprovedWithAppointee;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.applicationSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEdit;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationApprovedPendingPartnerAcceptance extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //
        stateActions.add(applicationCompleteApprovedWithAppointee(state)); //
        stateActions.add(applicationEscalate(APPLICATION_APPROVED_COMPLETED)); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_PARTNER_APPROVAL) //
                .withRaisesUrgentFlag() //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR, INSTITUTION_APPROVER, DEPARTMENT_ADMINISTRATOR, DEPARTMENT_APPROVER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_APPROVED)
                        .withTransitionAction(APPLICATION_CONFIRM_OFFER) //
                        .withStateTransitionEvaluation(APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE) //
                                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME) //
                                .withRoleTransitions(APPLICATION_CREATE_APPOINTEE_GROUP)));

        stateActions.add(applicationSendMessageApproved()); //
        stateActions.add(applicationViewEdit()); //
    }

}
