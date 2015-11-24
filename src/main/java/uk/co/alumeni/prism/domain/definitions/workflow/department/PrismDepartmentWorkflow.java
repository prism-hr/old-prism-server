package uk.co.alumeni.prism.domain.definitions.workflow.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.DEPARTMENT_MANAGE_USERS_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_DISABLED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_WITHDRAWN;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismDepartmentWorkflow {

    public static PrismStateAction departmentCreateProgram() {
        return new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_CREATE_PROGRAM) //
                .withActionCondition(PrismActionCondition.ACCEPT_PROGRAM) //
                .withStateTransitions(PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction departmentCreateProject() {
        return new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_CREATE_PROJECT) //
                .withActionCondition(PrismActionCondition.ACCEPT_PROJECT) //
                .withStateTransitions(PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION //
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
                .withAction(PrismAction.DEPARTMENT_ESCALATE) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_REJECTED) //
                        .withTransitionAction(PrismAction.DEPARTMENT_ESCALATE));
    }

    public static PrismStateAction departmentTerminateUnapproved() {
        return departmentTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_REJECTED) //
                        .withTransitionAction(PrismAction.DEPARTMENT_TERMINATE));
    }

    public static PrismStateAction departmentTerminateApproved() {
        return departmentTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_DISABLED_COMPLETED) //
                        .withTransitionAction(PrismAction.DEPARTMENT_TERMINATE));
    }

    public static PrismStateAction departmentViewEditApproval(PrismState state) {
        return departmentViewEditAbstract()
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PrismAction.DEPARTMENT_VIEW_EDIT)
                        .withRoleTransitions(DEPARTMENT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction departmentViewEditApproved() {
        return departmentViewEditAbstract() //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withAssignments(DEPARTMENT_VIEWER_GROUP, PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER) //
                .withStateTransitions(PrismStateTransitionGroup.DEPARTMENT_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(DEPARTMENT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction departmentViewEditInactive() {
        return departmentViewEditAbstract() //
                .withActionEnhancement(PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction departmentWithdraw() {
        return new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_WITHDRAW) //
                .withAssignments(PrismRole.DEPARTMENT_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_WITHDRAWN) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST));
    }

    public static PrismStateAction departmentViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_VIEW_EDIT);
    }

    private static PrismStateAction departmentEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_EMAIL_CREATOR) //
                .withAssignments(DEPARTMENT_PARENT_ADMINISTRATOR_GROUP);
    }

    private static PrismStateAction departmentTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.DEPARTMENT_TERMINATE);
    }

}
