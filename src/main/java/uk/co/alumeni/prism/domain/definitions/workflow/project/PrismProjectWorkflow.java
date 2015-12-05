package uk.co.alumeni.prism.domain.definitions.workflow.project;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_EMAIL_CREATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_ESCALATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_TERMINATE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.PROJECT_WITHDRAW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.INSTITUTION_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROGRAM_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROJECT_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.PROJECT_VIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_MANAGE_USERS_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_DISABLED_COMPLETED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_REJECTED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.PROJECT_WITHDRAWN;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_VIEW_EDIT_TRANSITION;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;

public class PrismProjectWorkflow {

    public static PrismStateAction projectEmailCreatorUnnapproved() {
        return projectEmailCreatorAbstract();
    }

    public static PrismStateAction projectEmailCreatorApproved() {
        return projectEmailCreatorAbstract() //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction projectEscalateUnapproved() {
        return projectEscalateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_REJECTED) //
                        .withTransitionAction(PROJECT_ESCALATE));
    }

    public static PrismStateAction projectEscalateApproved() {
        return projectEscalateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_DISABLED_COMPLETED) //
                        .withTransitionAction(PROJECT_ESCALATE));
    }

    public static PrismStateAction projectTerminateUnapproved() {
        return projectTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_REJECTED) //
                        .withTransitionAction(PROJECT_TERMINATE));
    }

    public static PrismStateAction projectTerminateApproved() {
        return projectTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_DISABLED_COMPLETED) //
                        .withTransitionAction(PROJECT_TERMINATE));
    }

    public static PrismStateAction projectViewEditApproval(PrismState state) {
        return projectViewEditAbstract()
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PROJECT_VIEW_EDIT)
                        .withRoleTransitions(PROJECT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction projectViewEditApproved() {
        return projectViewEditAbstract()
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_EDIT_AS_USER) //
                .withAssignments(INSTITUTION_VIEWER_GROUP, PROJECT_VIEW_AS_USER) //
                .withAssignments(DEPARTMENT_VIEWER_GROUP, PROJECT_VIEW_AS_USER) //
                .withAssignments(PROGRAM_VIEWER_GROUP, PROJECT_VIEW_AS_USER) //
                .withAssignments(PROJECT_VIEWER_GROUP, PROJECT_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_VIEWER_GROUP, PROJECT_VIEW_AS_USER) //
                .withStateTransitions(PROJECT_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PROJECT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction projectViewEditInactive() {
        return projectViewEditAbstract()
                .withActionEnhancement(PROJECT_VIEW_AS_USER) //
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction projectWithdraw() {
        return new PrismStateAction() //
                .withAction(PROJECT_WITHDRAW) //
                .withAssignments(PROJECT_ADMINISTRATOR)
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST));
    }

    public static PrismStateAction projectViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_VIEW_EDIT);
    }

    private static PrismStateAction projectEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_EMAIL_CREATOR) //
                .withAssignments(PROJECT_PARENT_ADMINISTRATOR_GROUP);
    }

    private static PrismStateAction projectEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_ESCALATE);
    }

    private static PrismStateAction projectTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_TERMINATE);
    }

}
