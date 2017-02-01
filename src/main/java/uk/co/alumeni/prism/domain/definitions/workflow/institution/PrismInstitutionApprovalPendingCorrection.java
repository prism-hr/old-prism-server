package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_CORRECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.INSTITUTION_CORRECT_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.INSTITUTION_REVIVE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.INSTITUTION_APPROVAL;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionApproval.institutionCompleteApproval;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.*;

public class PrismInstitutionApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(institutionCompleteApproval());

        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTION_CORRECT) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(INSTITUTION_CORRECT_REQUEST) //
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_APPROVAL) //
                        .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST) //
                        .withRoleTransitions(INSTITUTION_REVIVE_ADMINISTRATOR_GROUP))); //

        stateActions.add(institutionCreateProject());
        stateActions.add(institutionCreateProgram());
        stateActions.add(institutionCreateDepartment());
        stateActions.add(institutionSendMessageUnnapproved()); //
        stateActions.add(institutionEscalateUnapproved()); //
        stateActions.add(institutionTerminateUnapproved()); //
        stateActions.add(institutionViewEditApproval(state)); //
        stateActions.add(institutionWithdraw());
    }

}
