package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_COMPLETE_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.INSTITUTION_COMPLETE_APPROVAL_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL_PENDING_CORRECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_APPROVE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.*;

public class PrismInstitutionApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(institutionCompleteApproval() //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(INSTITUTION_COMPLETE_APPROVAL_STAGE_REQUEST) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_APPROVAL_PENDING_CORRECTION) //
                        .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST)
                        .withStateTransitionEvaluation(INSTITUTION_APPROVED_OUTCOME))); //

        stateActions.add(institutionCreateDepartment());
        stateActions.add(institutionCreateProgram());
        stateActions.add(institutionCreateProject());
        stateActions.add(institutionSendMessageUnnapproved()); //
        stateActions.add(institutionEscalateUnapproved()); //
        stateActions.add(institutionTerminateUnapproved()); //
        stateActions.add(institutionViewEditApproval(state)); //
        stateActions.add(institutionWithdraw());
    }

    public static PrismStateAction institutionCompleteApproval() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_COMPLETE_APPROVAL_STAGE) //
                .withStateActionAssignments(SYSTEM_ADMINISTRATOR) //
                .withStateTransitions(INSTITUTION_APPROVE_TRANSITION);
    }

}
