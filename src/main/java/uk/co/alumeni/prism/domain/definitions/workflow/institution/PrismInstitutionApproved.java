package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_CREATE_APPLICATION) //
                .withActionCondition(PrismActionCondition.ACCEPT_APPLICATION) //
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(PrismInstitutionWorkflow.institutionCreateDepartment());
        stateActions.add(PrismInstitutionWorkflow.institutionCreateProgram());
        stateActions.add(PrismInstitutionWorkflow.institutionCreateProject());
        stateActions.add(PrismInstitutionWorkflow.institutionEmailCreatorApproved()); //

        stateActions.add(PrismInstitutionWorkflow.institutionTerminateApproved());
        stateActions.add(PrismInstitutionWorkflow.institutionViewEditApproved()); //
    }

}
