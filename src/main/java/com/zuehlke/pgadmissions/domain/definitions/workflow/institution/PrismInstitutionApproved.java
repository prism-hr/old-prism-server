package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.DEPARTMENT_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.institution.PrismInstitutionWorkflow.institutionViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismInstitutionApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_DEPARTMENT) //
                .withActionCondition(ACCEPT_DEPARTMENT) //
                .withTransitions(DEPARTMENT_CREATE_TRANSITION //
                        .withRoleTransitions(DEPARTMENT_CREATE_ADMINISTRATOR_GROUP))); //

        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_PROGRAM) //
                .withActionCondition(ACCEPT_PROGRAM) //
                .withTransitions(PROGRAM_CREATE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP))); //

        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withTransitions(PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP))); //

        stateActions.add(new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_APPLICATION) //
                .withActionCondition(ACCEPT_APPLICATION)
                .withTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(institutionEmailCreatorApproved()); //
        stateActions.add(institutionTerminateApproved());
        stateActions.add(institutionViewEditApproved()); //
    }

}
