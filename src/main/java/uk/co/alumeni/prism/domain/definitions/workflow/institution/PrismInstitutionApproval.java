package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_COMPLETE_APPROVAL_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL_PENDING_CORRECTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_APPROVE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateDepartment;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateProgram;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateProject;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEscalateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionSendMessageUnnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionTerminateUnapproved;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionWithdraw;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(institutionCompleteApproval() //
                .withRaisesUrgentFlag() //
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
                .withNotifications(INSTITUTION_ADMINISTRATOR, INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION) //
                .withStateTransitions(INSTITUTION_APPROVE_TRANSITION);
    }

}
