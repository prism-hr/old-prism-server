package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.INSTITUTION_COMPLETE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.INSTITUTION_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_COMPLETE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.institution.PrismInstitutionWorkflow.*;

public class PrismInstitutionUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTION_COMPLETE) //
                .withStateTransitions(INSTITUTION_COMPLETE_TRANSITION //
                        .withRoleTransitions(INSTITUTION_CREATE_ADMINISTRATOR_GROUP)));

        stateActions.add(institutionCreateDepartment());
        stateActions.add(institutionCreateProgram());
        stateActions.add(institutionCreateProject());
    }

}
