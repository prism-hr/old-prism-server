package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.PrismActionResolution.RESOLVE_ENDORSEMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_ENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_IMPORT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_IMPORT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_UNENDORSE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_DEPARTMENT_TASK_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_DEPARTMENT_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_CREATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentEmailCreatorApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentTerminateApproved;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.department.PrismDepartmentWorkflow.departmentViewEditActive;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismDepartmentApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_APPLICATION) //
                .withActionCondition(ACCEPT_APPLICATION) //
                .withTransitions(APPLICATION_CREATE_TRANSITION //
                        .withRoleTransitions(APPLICATION_CREATE_CREATOR_GROUP))); //

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_PROGRAM) //
                .withActionCondition(ACCEPT_DEPARTMENT) //
                .withTransitions(PROGRAM_CREATE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP))); //

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withTransitions(PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP))); //

        stateActions.add(departmentEmailCreatorApproved());

        stateActions.add(new PrismStateAction() //
                .withActionResolution(DEPARTMENT_ENDORSE, RESOLVE_ENDORSEMENT, DEPARTMENT_UNENDORSE) //
                .withRaisesUrgentFlag() //
                .withNotification(SYSTEM_DEPARTMENT_TASK_REQUEST) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withNotifications(DEPARTMENT_ADMINISTRATOR_GROUP, SYSTEM_DEPARTMENT_UPDATE_NOTIFICATION));

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_IMPORT_PROGRAM) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_APPROVED) //
                        .withTransitionAction(DEPARTMENT_IMPORT_PROGRAM)));

        stateActions.add(new PrismStateAction() //
                .withAction(DEPARTMENT_IMPORT_PROJECT) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_APPROVED) //
                        .withTransitionAction(DEPARTMENT_IMPORT_PROJECT)));

        stateActions.add(departmentTerminateApproved()); //
        stateActions.add(departmentViewEditActive()); //
    }

}
