package uk.co.alumeni.prism.domain.definitions.workflow.department;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_CREATE_PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_ESCALATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_TERMINATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.DEPARTMENT_WITHDRAW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_SEND_MESSAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_STAFF_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PARTNERSHIP_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.DEPARTMENT_MANAGE_USERS_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_DISABLED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.DEPARTMENT_WITHDRAWN;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_VIEW_EDIT_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;

public class PrismDepartmentWorkflow {

    public static PrismStateAction departmentCreateProgram() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_PROGRAM) //
                .withActionCondition(ACCEPT_PROGRAM) //
                .withStateTransitions(PROGRAM_CREATE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction departmentCreateProject() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withStateTransitions(PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction departmentSendMessageUnnapproved() {
        return departmentSendMessageAbstract();
    }

    public static PrismStateAction departmentSendMessageApproved() {
        return departmentSendMessageAbstract() //
                .withAssignment(DEPARTMENT_ADMINISTRATOR, DEPARTMENT_STAFF_GROUP) //
                .withAssignment(DEPARTMENT_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withAssignments(DEPARTMENT_STAFF_GROUP, DEPARTMENT_ADMINISTRATOR) //
                .withPartnerAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withPartnerRecipientAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PARTNERSHIP_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction departmentEscalateUnapproved() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_ESCALATE) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_REJECTED) //
                        .withTransitionAction(DEPARTMENT_ESCALATE));
    }

    public static PrismStateAction departmentTerminateUnapproved() {
        return departmentTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_REJECTED) //
                        .withTransitionAction(DEPARTMENT_TERMINATE));
    }

    public static PrismStateAction departmentTerminateApproved() {
        return departmentTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_DISABLED_COMPLETED) //
                        .withTransitionAction(DEPARTMENT_TERMINATE));
    }

    public static PrismStateAction departmentViewEditApproval(PrismState state) {
        return departmentViewEditAbstract()
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(DEPARTMENT_VIEW_EDIT)
                        .withRoleTransitions(DEPARTMENT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction departmentViewEditApproved() {
        return departmentViewEditAbstract() //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withAssignments(DEPARTMENT_VIEWER_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withStateTransitions(DEPARTMENT_VIEW_EDIT_TRANSITION //
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
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(DEPARTMENT_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_DEPARTMENT_LIST));
    }

    public static PrismStateAction departmentViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_VIEW_EDIT);
    }

    private static PrismStateAction departmentSendMessageAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_SEND_MESSAGE) //
                .withAssignments(DEPARTMENT_PARENT_ADMINISTRATOR_GROUP, DEPARTMENT_ADMINISTRATOR);
    }

    private static PrismStateAction departmentTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_TERMINATE);
    }

}
