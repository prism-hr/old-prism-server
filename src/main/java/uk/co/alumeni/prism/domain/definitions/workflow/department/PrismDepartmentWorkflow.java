package uk.co.alumeni.prism.domain.definitions.workflow.department;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.DEPARTMENT_ENQUIRER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.*;

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
                .withStateActionAssignment(DEPARTMENT_ADMINISTRATOR, DEPARTMENT_STAFF_GROUP) //
                .withStateActionAssignment(DEPARTMENT_ADMINISTRATOR, DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(DEPARTMENT_STAFF_GROUP, DEPARTMENT_ADMINISTRATOR) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, DEPARTMENT_ADMINISTRATOR_GROUP) //
                .withPartnerStateActionRecipientAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PARTNERSHIP_ADMINISTRATOR_GROUP);
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
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(DEPARTMENT_VIEW_EDIT)
                        .withRoleTransitions(DEPARTMENT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction departmentViewEditApproved() {
        return departmentViewEditAbstract() //
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(DEPARTMENT_VIEWER_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withStateActionAssignments(DEPARTMENT_ENQUIRER, DEPARTMENT_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(DEPARTMENT_STAFF_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(INSTITUTION_STAFF_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withStateTransitions(DEPARTMENT_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(DEPARTMENT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction departmentViewEditInactive() {
        return departmentViewEditAbstract() //
                .withActionEnhancement(DEPARTMENT_VIEW_AS_USER) //
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction departmentWithdraw() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_WITHDRAW) //
                .withStateActionAssignments(DEPARTMENT_ADMINISTRATOR) //
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
                .withAction(DEPARTMENT_SEND_MESSAGE) //
                .withStateActionAssignments(DEPARTMENT_PARENT_ADMINISTRATOR_GROUP, DEPARTMENT_ADMINISTRATOR);
    }

    private static PrismStateAction departmentTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_TERMINATE);
    }

}
