package uk.co.alumeni.prism.domain.definitions.workflow.program;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.*;

public class PrismProgramWorkflow {

    public static PrismStateAction programCreateProject() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_CREATE_PROJECT) //
                .withActionCondition(PrismActionCondition.ACCEPT_PROJECT) //
                .withStateTransitions(PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction programEmailCreatorUnnapproved() {
        return programEmailCreatorAbstract();
    }

    public static PrismStateAction programEmailCreatorApproved() {
        return programEmailCreatorAbstract() //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction programEscalateUnapproved() {
        return programEscalateAbstract() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_REJECTED) //
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE));
    }

    public static PrismStateAction programTerminateUnapproved() {
        return programTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_REJECTED) //
                        .withTransitionAction(PrismAction.PROGRAM_TERMINATE));
    }

    public static PrismStateAction programTerminateApproved() {
        return programTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) //
                        .withTransitionAction(PrismAction.PROGRAM_TERMINATE));
    }

    public static PrismStateAction programViewEditApproval(PrismState state) {
        return programViewEditAbstract()
                .withAssignments(PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT)
                        .withRoleTransitions(PrismRoleTransitionGroup.PROGRAM_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction programViewEditApproved() {
        return programViewEditAbstract() //
                .withAssignments(PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER) //
                .withAssignments(PrismRoleGroup.INSTITUTION_VIEWER_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.DEPARTMENT_VIEWER_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.PROGRAM_VIEWER_GROUP, PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_VIEWER_GROUP, PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withStateTransitions(PrismStateTransitionGroup.PROGRAM_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROGRAM_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction programViewEditInactive() {
        return programViewEditAbstract() //
                .withActionEnhancement(PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction programWithdraw() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_WITHDRAW) //
                .withAssignments(PrismRole.PROGRAM_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROGRAM_WITHDRAWN) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST));
    }

    public static PrismStateAction programViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_VIEW_EDIT);
    }

    private static PrismStateAction programEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_EMAIL_CREATOR) //
                .withAssignments(PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP);
    }

    private static PrismStateAction programEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_ESCALATE);
    }

    private static PrismStateAction programTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROGRAM_TERMINATE);
    }

}
