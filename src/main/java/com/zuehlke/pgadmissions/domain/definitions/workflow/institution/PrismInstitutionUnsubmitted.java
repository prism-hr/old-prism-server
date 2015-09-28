package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTON_COMPLETE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateDepartment;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateProgram;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionCreateProject;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionUnsubmitted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTON_COMPLETE) //
                .withTransitions(INSTITUTION_CREATE_TRANSITION));
        
        stateActions.add(institutionCreateDepartment());
        stateActions.add(institutionCreateProgram());
        stateActions.add(institutionCreateProject());
    }

}
