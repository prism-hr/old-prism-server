package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_COMPLETE) //
                .withStateTransitions(PrismStateTransitionGroup.INSTITUTION_COMPLETE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.INSTITUTION_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(PrismInstitutionWorkflow.institutionCreateDepartment());
        stateActions.add(PrismInstitutionWorkflow.institutionCreateProgram());
        stateActions.add(PrismInstitutionWorkflow.institutionCreateProject());
    }

}
