package uk.co.alumeni.prism.domain.definitions.workflow.project;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROJECT_ENQUIRER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_MANAGE_USERS_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_VIEW_EDIT_TRANSITION;

public class PrismProjectWorkflow {

    public static PrismStateAction projectSendMessageUnnapproved() {
        return projectSendMessageAbstract();
    }

    public static PrismStateAction projectSendMessageApproved() {
        return projectSendMessageAbstract() //
                .withStateActionAssignment(PROJECT_ADMINISTRATOR, PROJECT_STAFF_GROUP) //
                .withStateActionAssignment(PROJECT_ADMINISTRATOR, PROJECT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(PROJECT_STAFF_GROUP, PROJECT_ADMINISTRATOR) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, PROJECT_ADMINISTRATOR_GROUP) //
                .withPartnerStateActionRecipientAssignments(PROJECT_ADMINISTRATOR_GROUP, PARTNERSHIP_ADMINISTRATOR_GROUP);
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
                .withStateActionAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_EDIT_AS_USER) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PROJECT_VIEW_EDIT)
                        .withRoleTransitions(PROJECT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction projectViewEditApproved() {
        return projectViewEditAbstract()
                .withStateActionAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(INSTITUTION_STAFF_GROUP, PROJECT_VIEW_AS_USER) //
                .withStateActionAssignments(DEPARTMENT_STAFF_GROUP, PROJECT_VIEW_AS_USER) //
                .withStateActionAssignments(PROJECT_STAFF_GROUP, PROJECT_VIEW_AS_USER) //
                .withStateActionAssignments(PROJECT_ENQUIRER, PROJECT_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(INSTITUTION_STAFF_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(DEPARTMENT_STAFF_GROUP, PROGRAM_VIEW_AS_USER) //
                .withStateTransitions(PROJECT_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PROJECT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction projectViewEditInactive() {
        return projectViewEditAbstract()
                .withActionEnhancement(PROJECT_VIEW_AS_USER) //
                .withStateActionAssignments(PROJECT_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction projectWithdraw() {
        return new PrismStateAction() //
                .withAction(PROJECT_WITHDRAW) //
                .withStateActionAssignments(PROJECT_ADMINISTRATOR)
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST));
    }

    public static PrismStateAction projectViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_VIEW_EDIT);
    }

    private static PrismStateAction projectSendMessageAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_SEND_MESSAGE) //
                .withStateActionAssignments(PROJECT_PARENT_ADMINISTRATOR_GROUP, PROJECT_ADMINISTRATOR);
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
