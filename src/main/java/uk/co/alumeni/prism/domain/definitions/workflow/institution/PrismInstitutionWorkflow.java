package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismInstitutionWorkflow {

    public static PrismStateAction institutionCreateDepartment() {
        return new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_CREATE_DEPARTMENT) //
                .withActionCondition(PrismActionCondition.ACCEPT_DEPARTMENT) //
                .withStateTransitions(PrismStateTransitionGroup.DEPARTMENT_CREATE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.DEPARTMENT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionCreateProject() {
        return new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_CREATE_PROJECT) //
                .withActionCondition(PrismActionCondition.ACCEPT_PROJECT) //
                .withStateTransitions(PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionCreateProgram() {
        return new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_CREATE_PROGRAM) //
                .withActionCondition(PrismActionCondition.ACCEPT_PROGRAM) //
                .withStateTransitions(PrismStateTransitionGroup.PROGRAM_CREATE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROGRAM_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionEmailCreatorUnnapproved() {
        return institutionEmailCreatorAbstract();
    }

    public static PrismStateAction institutionEmailCreatorApproved() {
        return institutionEmailCreatorAbstract() //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction institutionEscalateUnapproved() {
        return new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_ESCALATE) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_REJECTED) //
                        .withTransitionAction(PrismAction.INSTITUTION_ESCALATE));
    }

    public static PrismStateAction institutionTerminateUnapproved() {
        return institutionTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_REJECTED) //
                        .withTransitionAction(PrismAction.INSTITUTION_TERMINATE));
    }

    public static PrismStateAction institutionTerminateApproved() {
        return institutionTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_DISABLED_COMPLETED) //
                        .withTransitionAction(PrismAction.INSTITUTION_TERMINATE));
    }

    public static PrismStateAction institutionViewEditApproval(PrismState state) {
        return institutionViewEditAbstract()
                .withAssignments(PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP, PrismActionEnhancement.INSTITUTION_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.INSTITUTION_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT)
                        .withRoleTransitions(PrismRoleTransitionGroup.INSTITUTION_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction institutionViewEditApproved() {
        return institutionViewEditAbstract()
                .withAssignments(PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP, PrismActionEnhancement.INSTITUTION_VIEW_EDIT_AS_USER) //
                .withAssignments(PrismRoleGroup.INSTITUTION_VIEWER_GROUP, PrismActionEnhancement.INSTITUTION_VIEW_AS_USER) //
                .withStateTransitions(PrismStateTransitionGroup.INSTITUTION_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.INSTITUTION_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction institutionViewEditInactive() {
        return institutionViewEditAbstract() //
                .withActionEnhancement(PrismActionEnhancement.INSTITUTION_VIEW_AS_USER)
                .withAssignments(PrismRoleGroup.INSTITUTION_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction institutionWithdraw() {
        return new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_WITHDRAW) //
                .withAssignments(PrismRole.INSTITUTION_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_WITHDRAWN) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST));
    }

    public static PrismStateAction institutionViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_VIEW_EDIT);
    }

    private static PrismStateAction institutionEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_EMAIL_CREATOR) //
                .withAssignments(PrismRole.SYSTEM_ADMINISTRATOR);
    }

    private static PrismStateAction institutionTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.INSTITUTION_TERMINATE);
    }

}
