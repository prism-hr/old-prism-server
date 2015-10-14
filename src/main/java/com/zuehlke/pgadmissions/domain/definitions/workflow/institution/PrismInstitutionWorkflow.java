package com.zuehlke.pgadmissions.domain.definitions.workflow.institution;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_CREATE_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.INSTITUTION_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.SYSTEM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_VIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.DEPARTMENT_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.INSTITUTION_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.DEPARTMENT_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.INSTITUTION_VIEW_EDIT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismInstitutionWorkflow {

    public static PrismStateAction institutionCreateDepartment() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_DEPARTMENT) //
                .withActionCondition(ACCEPT_DEPARTMENT) //
                .withTransitions(DEPARTMENT_CREATE_TRANSITION //
                        .withRoleTransitions(DEPARTMENT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionCreateProject() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withTransitions(PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionCreateProgram() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_PROGRAM) //
                .withActionCondition(ACCEPT_PROGRAM) //
                .withTransitions(PROGRAM_CREATE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionEmailCreatorUnnapproved() {
        return institutionEmailCreatorAbstract();
    }

    public static PrismStateAction institutionEmailCreatorApproved() {
        return institutionEmailCreatorAbstract() //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction institutionEscalateUnapproved() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_ESCALATE) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_REJECTED) //
                        .withTransitionAction(INSTITUTION_ESCALATE));
    }

    public static PrismStateAction institutionTerminateUnapproved() {
        return institutionTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_REJECTED) //
                        .withTransitionAction(INSTITUTION_TERMINATE));
    }

    public static PrismStateAction institutionTerminateApproved() {
        return institutionTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_DISABLED_COMPLETED) //
                        .withTransitionAction(INSTITUTION_TERMINATE));
    }

    public static PrismStateAction institutionViewEditApproval(PrismState state) {
        return institutionViewEditAbstract()
                .withAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(INSTITUTION_VIEW_EDIT)
                        .withRoleTransitions(INSTITUTION_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction institutionViewEditApproved() {
        return institutionViewEditAbstract()
                .withAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_EDIT_AS_USER) //
                .withAssignments(INSTITUTION_VIEWER_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withTransitions(INSTITUTION_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(INSTITUTION_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction institutionViewEditInactive() {
        return institutionViewEditAbstract() //
                .withActionEnhancement(INSTITUTION_VIEW_AS_USER)
                .withAssignments(INSTITUTION_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction institutionWithdraw() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_WITHDRAW) //
                .withAssignments(INSTITUTION_ADMINISTRATOR) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST));
    }

    private static PrismStateAction institutionEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_EMAIL_CREATOR) //
                .withAssignments(SYSTEM_ADMINISTRATOR);
    }

    private static PrismStateAction institutionTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_TERMINATE);
    }

    private static PrismStateAction institutionViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_VIEW_EDIT);
    }

}
