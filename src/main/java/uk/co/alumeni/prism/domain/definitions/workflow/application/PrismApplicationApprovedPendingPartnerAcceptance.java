package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_PARTNER_APPROVAL_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_APPOINTEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_HIRING_MANAGER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.applicationCompleteApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationApproved.applicationSendMessageApproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;

public class PrismApplicationApprovedPendingPartnerAcceptance extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationComment()); //
        stateActions.add(applicationCompleteApproved(state)); //
        stateActions.add(applicationEscalate(APPLICATION_ACCEPTED)); //

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_PARTNER_APPROVAL) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(APPLICATION_PROVIDE_PARTNER_APPROVAL_REQUEST) //
                .withPartnerStateActionAssignments(INSTITUTION_ADMINISTRATOR, INSTITUTION_APPROVER, DEPARTMENT_ADMINISTRATOR, DEPARTMENT_APPROVER) //
                .withStateTransitions(new PrismStateTransition() //
                                .withTransitionState(APPLICATION_APPROVED)
                                .withTransitionAction(APPLICATION_CONFIRM_OFFER) //
                                .withStateTransitionEvaluation(APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_APPROVED_PENDING_OFFER_ACCEPTANCE) //
                                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(APPLICATION_PROVIDED_PARTNER_APPROVAL_OUTCOME) //
                                .withRoleTransitions(APPLICATION_CREATE_HIRING_MANAGER_GROUP, APPLICATION_CREATE_APPOINTEE_GROUP)));

        stateActions.add(applicationSendMessageApproved()); //
        stateActions.add(PrismApplicationWorkflow.applicationViewEdit()); //
    }

}
