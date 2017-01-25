package uk.co.alumeni.prism.domain.definitions.workflow.institution;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCondition.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.INSTITUTION_VIEW_EDIT_AS_USER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.*;

public class PrismInstitutionWorkflow {

    public static PrismStateAction institutionCreateDepartment() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_DEPARTMENT) //
                .withActionCondition(ACCEPT_DEPARTMENT) //
                .withStateTransitions(DEPARTMENT_CREATE_TRANSITION //
                        .withRoleTransitions(DEPARTMENT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionCreateProject() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withStateTransitions(PROJECT_CREATE_TRANSITION //
                        .withRoleTransitions(PROJECT_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionCreateProgram() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_CREATE_PROGRAM) //
                .withActionCondition(ACCEPT_PROGRAM) //
                .withStateTransitions(PROGRAM_CREATE_TRANSITION //
                        .withRoleTransitions(PROGRAM_CREATE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction institutionSendMessageUnnapproved() {
        return institutionSendMessageAbstract();
    }

    public static PrismStateAction institutionSendMessageApproved() {
        return institutionSendMessageAbstract() //
                .withStateActionAssignment(INSTITUTION_ADMINISTRATOR, INSTITUTION_STAFF_GROUP) //
                .withStateActionAssignment(INSTITUTION_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(INSTITUTION_STAFF_GROUP, INSTITUTION_ADMINISTRATOR) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, INSTITUTION_ADMINISTRATOR_GROUP) //
                .withPartnerStateActionRecipientAssignments(INSTITUTION_ADMINISTRATOR_GROUP, PARTNERSHIP_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction institutionEscalateUnapproved() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_ESCALATE) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_REJECTED) //
                        .withTransitionAction(INSTITUTION_ESCALATE));
    }

    public static PrismStateAction institutionTerminateUnapproved() {
        return institutionTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_REJECTED) //
                        .withTransitionAction(INSTITUTION_TERMINATE));
    }

    public static PrismStateAction institutionTerminateApproved() {
        return institutionTerminateAbstract()
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_DISABLED_COMPLETED) //
                        .withTransitionAction(INSTITUTION_TERMINATE));
    }

    public static PrismStateAction institutionViewEditApproval(PrismState state) {
        return institutionViewEditAbstract()
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_EDIT_AS_USER) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(INSTITUTION_VIEW_EDIT)
                        .withRoleTransitions(INSTITUTION_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction institutionViewEditApproved() {
        return institutionViewEditAbstract()
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_EDIT_AS_USER) //
                .withStateActionAssignments(INSTITUTION_VIEWER_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withStateActionAssignments(INSTITUTION_ENQUIRER, INSTITUTION_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(PARTNERSHIP_ADMINISTRATOR_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(DEPARTMENT_STAFF_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withPartnerStateActionAssignments(INSTITUTION_STAFF_GROUP, INSTITUTION_VIEW_AS_USER) //
                .withStateTransitions(INSTITUTION_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(INSTITUTION_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction institutionViewEditInactive() {
        return institutionViewEditAbstract() //
                .withActionEnhancement(INSTITUTION_VIEW_AS_USER)
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR_GROUP);
    }

    public static PrismStateAction institutionWithdraw() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_WITHDRAW) //
                .withStateActionAssignments(INSTITUTION_ADMINISTRATOR) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(INSTITUTION_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_INSTITUTION_LIST));
    }

    public static PrismStateAction institutionViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_VIEW_EDIT);
    }

    private static PrismStateAction institutionSendMessageAbstract() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_SEND_MESSAGE) //
                .withStateActionAssignment(SYSTEM_ADMINISTRATOR, INSTITUTION_ADMINISTRATOR);
    }

    private static PrismStateAction institutionTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(INSTITUTION_TERMINATE);
    }

}
