package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismInstitutionApproval.institutionCompleteApproval());

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_CORRECT) //
                .withRaisesUrgentFlag() //
                .withNotification(PrismNotificationDefinition.INSTITUTION_CORRECT_REQUEST) //
                .withStateActionAssignments(PrismRole.INSTITUTION_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_APPROVAL) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST) //
                        .withRoleTransitions(PrismRoleTransitionGroup.INSTITUTION_REVIVE_ADMINISTRATOR_GROUP))); //

        stateActions.add(PrismInstitutionWorkflow.institutionCreateProject());
        stateActions.add(PrismInstitutionWorkflow.institutionCreateProgram());
        stateActions.add(PrismInstitutionWorkflow.institutionCreateDepartment());
        stateActions.add(PrismInstitutionWorkflow.institutionEmailCreatorUnnapproved()); //
        stateActions.add(PrismInstitutionWorkflow.institutionEscalateUnapproved()); //
        stateActions.add(PrismInstitutionWorkflow.institutionTerminateUnapproved()); //
        stateActions.add(PrismInstitutionWorkflow.institutionViewEditApproval(state)); //
        stateActions.add(PrismInstitutionWorkflow.institutionWithdraw());
    }

}
