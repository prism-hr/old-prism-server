package uk.co.alumeni.prism.domain.definitions.workflow.program;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PROGRAM_ENQUIRER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_MANAGE_USERS_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_VIEW_EDIT_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;

public class PrismProgramWorkflow {

    public static PrismStateAction programCreateProject() {
        return new PrismStateAction() //
                .withAction(PROGRAM_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withStateTransitions(PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction programSendMessageUnnapproved() {
        return programSendMessageAbstract();
    }

    public static PrismStateAction programSendMessageApproved() {
        return programSendMessageAbstract() //
                .withStateActionAssignment(PROGRAM_ADMINISTRATOR, PROGRAM_STAFF_GROUP) //
                .withStateActionAssignment(PROGRAM_ADMINISTRATOR, PROGRAM_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(PROGRAM_STAFF_GROUP, PROGRAM_ADMINISTRATOR) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, PROGRAM_ADMINISTRATOR_GROUP) //
                .withPartnerStateActionRecipientAssignments(PROGRAM_ADMINISTRATOR_GROUP, PARTNERSHIP_ADMINISTRATOR_GROUP);
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
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, PROGRAM_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PROGRAM_VIEW_EDIT)
                        .withRoleTransitions(PROGRAM_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction programViewEditApproved() {
        return programViewEditAbstract() //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(INSTITUTION_STAFF_GROUP, PROJECT_VIEW_AS_USER) //
                .withStateActionAssignments(DEPARTMENT_STAFF_GROUP, PROJECT_VIEW_AS_USER) //
                .withStateActionAssignments(PROGRAM_STAFF_GROUP, PROGRAM_VIEW_AS_USER) //
                .withStateActionAssignments(PROGRAM_ENQUIRER, PROGRAM_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(INSTITUTION_STAFF_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(DEPARTMENT_STAFF_GROUP, PROGRAM_VIEW_AS_USER) //
                .withStateTransitions(PROGRAM_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PROGRAM_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction programViewEditInactive() {
        return programViewEditAbstract() //
                .withActionEnhancement(PROGRAM_VIEW_AS_USER) //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction programWithdraw() {
        return new PrismStateAction() //
                .withAction(PROGRAM_WITHDRAW) //
                .withStateActionAssignments(PROGRAM_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST));
    }

    public static PrismStateAction programViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PROGRAM_VIEW_EDIT);
    }

    private static PrismStateAction programSendMessageAbstract() {
        return new PrismStateAction() //
                .withAction(PROGRAM_SEND_MESSAGE) //
                .withStateActionAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP, PROGRAM_ADMINISTRATOR);
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
