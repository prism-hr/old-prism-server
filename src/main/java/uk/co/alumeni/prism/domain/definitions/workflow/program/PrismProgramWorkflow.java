package uk.co.alumeni.prism.domain.definitions.workflow.program;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_EMAIL_CREATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_ESCALATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_TERMINATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROGRAM_WITHDRAW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_STAFF_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_STAFF_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_MANAGE_USERS_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_VIEW_EDIT_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;

public class PrismProgramWorkflow {

    public static PrismStateAction programCreateProject() {
        return new PrismStateAction() //
                .withAction(PROGRAM_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withStateTransitions(PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction programEmailCreatorUnnapproved() {
        return programEmailCreatorAbstract();
    }

    public static PrismStateAction programEmailCreatorApproved() {
        return programEmailCreatorAbstract() //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction programEscalateUnapproved() {
        return programEscalateAbstract() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_REJECTED) //
                        .withTransitionAction(PROGRAM_ESCALATE));
    }

    public static PrismStateAction programEscalateApproved() {
        return programEscalateAbstract() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
                        .withTransitionAction(PROGRAM_ESCALATE));
    }

    public static PrismStateAction programTerminateUnapproved() {
        return programTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_REJECTED) //
                        .withTransitionAction(PROGRAM_TERMINATE));
    }

    public static PrismStateAction programTerminateApproved() {
        return programTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
                        .withTransitionAction(PROGRAM_TERMINATE));
    }

    public static PrismStateAction programViewEditApproval(PrismState state) {
        return programViewEditAbstract()
                .withAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROGRAM_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PROGRAM_VIEW_EDIT)
                        .withRoleTransitions(PROGRAM_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction programViewEditApproved() {
        return programViewEditAbstract() //
                .withAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withAssignments(INSTITUTION_STAFF_GROUP, PROJECT_VIEW_AS_USER) //
                .withAssignments(DEPARTMENT_STAFF_GROUP, PROJECT_VIEW_AS_USER) //
                .withAssignments(PROGRAM_VIEWER_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerAssignments(INSTITUTION_STAFF_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_STAFF_GROUP, PROGRAM_VIEW_AS_USER) //
                .withStateTransitions(PROGRAM_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PROGRAM_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction programViewEditInactive() {
        return programViewEditAbstract() //
                .withActionEnhancement(PROGRAM_VIEW_AS_USER) //
                .withAssignments(PROGRAM_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction programWithdraw() {
        return new PrismStateAction() //
                .withAction(PROGRAM_WITHDRAW) //
                .withAssignments(PROGRAM_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST));
    }

    public static PrismStateAction programViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PROGRAM_VIEW_EDIT);
    }

    private static PrismStateAction programEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(PROGRAM_EMAIL_CREATOR) //
                .withAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP);
    }

    private static PrismStateAction programEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(PROGRAM_ESCALATE);
    }

    private static PrismStateAction programTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(PROGRAM_TERMINATE);
    }

}
