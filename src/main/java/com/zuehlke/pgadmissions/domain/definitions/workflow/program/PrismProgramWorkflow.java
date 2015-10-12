package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_CREATE_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROGRAM_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_VIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_VIEW_EDIT_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_CREATE_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismProgramWorkflow {

    public static PrismStateAction programCreateProject() {
        return new PrismStateAction() //
                .withAction(PROGRAM_CREATE_PROJECT) //
                .withActionCondition(ACCEPT_PROJECT) //
                .withTransitions(PROJECT_CREATE_TRANSITION //
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
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_REJECTED) //
                        .withTransitionAction(PROGRAM_ESCALATE));
    }

    public static PrismStateAction programTerminateUnapproved() {
        return programTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_REJECTED) //
                        .withTransitionAction(PROGRAM_TERMINATE));
    }

    public static PrismStateAction programTerminateApproved() {
        return programTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_DISABLED_COMPLETED) //
                        .withTransitionAction(PROGRAM_TERMINATE));
    }

    public static PrismStateAction programViewEditApproval(PrismState state) {
        return programViewEditAbstract()
                .withAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROGRAM_VIEW_AS_USER) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PROGRAM_VIEW_EDIT)
                        .withRoleTransitions(PROGRAM_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction programViewEditApproved() {
        return programViewEditAbstract() //
                .withAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withAssignments(PROGRAM_VIEWER_GROUP, PROGRAM_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROGRAM_VIEW_AS_USER) //
                .withTransitions(PROGRAM_VIEW_EDIT_TRANSITION //
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
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST));
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

    private static PrismStateAction programViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PROGRAM_VIEW_EDIT);
    }

}
