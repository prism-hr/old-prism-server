package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_REENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_UNENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PARTNERSHIP_MANAGER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_ENDORSE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentCreateProgram;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentCreateProject;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditApproved;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_APPLICATION) //
                .withActionCondition(ACCEPT_APPLICATION) //
                .withStateTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(departmentCreateProgram()); //
        stateActions.add(departmentCreateProject()); //
        stateActions.add(departmentEmailCreatorApproved());

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_ENDORSE) //
                .withPartnerAssignments(PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(DEPARTMENT_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_UNENDORSE) //
                .withPartnerAssignments(PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(DEPARTMENT_ENDORSE_TRANSITION));

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_REENDORSE) //
                .withPartnerAssignments(PARTNERSHIP_MANAGER_GROUP) //
                .withStateTransitions(DEPARTMENT_ENDORSE_TRANSITION));

        stateActions.add(departmentTerminateApproved()); //
        stateActions.add(departmentViewEditApproved()); //
    }

}
