package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.DEPARTMENT_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_VIEW_EDIT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismDepartmentWorkflow {
    
    public static PrismStateAction departmentCreateProgram() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_PROGRAM) //
                .withActionCondition(ACCEPT_DEPARTMENT) //
                .withTransitions(PROGRAM_CREATE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP));
    }
    
    public static PrismStateAction departmentCreateProject() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withTransitions(PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP));
    }
    
    public static PrismStateAction departmentEmailCreatorUnnapproved() {
        return departmentEmailCreatorAbstract();
    }

    public static PrismStateAction departmentEmailCreatorApproved() {
        return departmentEmailCreatorAbstract() //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction departmentEscalateUnapproved() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_ESCALATE) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_REJECTED) //
                        .withTransitionAction(DEPARTMENT_ESCALATE));
    }

    public static PrismStateAction departmentTerminateUnapproved() {
        return departmentTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_REJECTED) //
                        .withTransitionAction(DEPARTMENT_TERMINATE));
    }

    public static PrismStateAction departmentTerminateApproved() {
        return departmentTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_DISABLED_COMPLETED) //
                        .withTransitionAction(DEPARTMENT_TERMINATE));
    }

    public static PrismStateAction departmentViewEditApproval(PrismState state) {
        return departmentViewEditAbstract()
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(DEPARTMENT_VIEW_EDIT)
                        .withRoleTransitions(DEPARTMENT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction departmentViewEditApproved() {
        return departmentViewEditAbstract() //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withTransitions(DEPARTMENT_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(DEPARTMENT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction departmentViewEditInactive() {
        return departmentViewEditAbstract() //
                .withActionEnhancement(DEPARTMENT_VIEW_AS_USER) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction departmentWithdraw() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_WITHDRAW) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST));
    }

    private static PrismStateAction departmentEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_EMAIL_CREATOR) //
                .withAssignments(DEPARTMENT_PARENT_ADMINISTRATOR_GROUP);
    }

    private static PrismStateAction departmentTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_TERMINATE);
    }

    private static PrismStateAction departmentViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_VIEW_EDIT);
    }

}
