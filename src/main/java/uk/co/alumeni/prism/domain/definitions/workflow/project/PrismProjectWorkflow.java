package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;

public class PrismProjectWorkflow {

    public static PrismStateAction projectEmailCreatorUnnapproved() {
        return projectEmailCreatorAbstract();
    }

    public static PrismStateAction projectEmailCreatorApproved() {
        return projectEmailCreatorAbstract() //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction projectEscalateUnapproved() {
        return projectEscalateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_REJECTED) //
                        .withTransitionAction(PrismAction.PROJECT_ESCALATE));
    }

    public static PrismStateAction projectTerminateUnapproved() {
        return projectTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_REJECTED) //
                        .withTransitionAction(PrismAction.PROJECT_TERMINATE));
    }

    public static PrismStateAction projectTerminateApproved() {
        return projectTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) //
                        .withTransitionAction(PrismAction.PROJECT_TERMINATE));
    }

    public static PrismStateAction projectViewEditApproval(PrismState state) {
        return projectViewEditAbstract()
                .withAssignments(PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PrismAction.PROJECT_VIEW_EDIT)
                        .withRoleTransitions(PrismRoleTransitionGroup.PROJECT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction projectViewEditApproved() {
        return projectViewEditAbstract()
                .withAssignments(PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER) //
                .withAssignments(PrismRoleGroup.INSTITUTION_VIEWER_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.DEPARTMENT_VIEWER_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.PROGRAM_VIEWER_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.PROJECT_VIEWER_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withPartnerAssignments(PrismRoleGroup.DEPARTMENT_VIEWER_GROUP, PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withStateTransitions(PrismStateTransitionGroup.PROJECT_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.PROJECT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction projectViewEditInactive() {
        return projectViewEditAbstract()
                .withActionEnhancement(PrismActionEnhancement.PROJECT_VIEW_AS_USER) //
                .withAssignments(PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction projectWithdraw() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROJECT_WITHDRAW) //
                .withAssignments(PrismRole.PROJECT_ADMINISTRATOR)
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.PROJECT_WITHDRAWN) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST));
    }

    public static PrismStateAction projectViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROJECT_VIEW_EDIT);
    }

    private static PrismStateAction projectEmailCreatorAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROJECT_EMAIL_CREATOR) //
                .withAssignments(PrismRoleGroup.PROJECT_PARENT_ADMINISTRATOR_GROUP);
    }

    private static PrismStateAction projectEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROJECT_ESCALATE);
    }

    private static PrismStateAction projectTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(PrismAction.PROJECT_TERMINATE);
    }

}
