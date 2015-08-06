package com.zuehlke.pgadmissions.domain.definitions.workflow.department;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.DEPARTMENT_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_DEPARTMENT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.DEPARTMENT_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_DEPARTMENT_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.DEPARTMENT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.DEPARTMENT_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.DEPARTMENT_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_VIEW_EDIT_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismDepartmentWorkflow {

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

    public static PrismStateAction departmentViewEditUnapproved() {
        return departmentViewEditAbstract() //
                .withActionEnhancement(DEPARTMENT_VIEW_AS_USER) //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction departmentViewEditApproved() {
        return departmentViewEditAbstract() //
                .withAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, DEPARTMENT_VIEW_AS_USER) //
                .withTransitions(DEPARTMENT_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(DEPARTMENT_MANAGE_USERS_GROUP));
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
                .withAction(DEPARTMENT_TERMINATE) //
                .withNotifications(DEPARTMENT_ADMINISTRATOR_GROUP, SYSTEM_DEPARTMENT_UPDATE_NOTIFICATION);
    }

    private static PrismStateAction departmentViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(DEPARTMENT_VIEW_EDIT);
    }

}
