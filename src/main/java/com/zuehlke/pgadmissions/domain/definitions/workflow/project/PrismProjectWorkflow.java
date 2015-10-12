package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_ESCALATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_TERMINATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_WITHDRAW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.DEPARTMENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_VIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DISABLED_COMPLETED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_VIEW_EDIT_TRANSITION;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

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
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_REJECTED) //
                        .withTransitionAction(PROJECT_ESCALATE));
    }

    public static PrismStateAction projectTerminateUnapproved() {
        return projectTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_REJECTED) //
                        .withTransitionAction(PROJECT_TERMINATE));
    }

    public static PrismStateAction projectTerminateApproved() {
        return projectTerminateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_DISABLED_COMPLETED) //
                        .withTransitionAction(PROJECT_TERMINATE));
    }

    public static PrismStateAction projectViewEditApproval(PrismState state) {
        return projectViewEditAbstract()
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state)
                        .withTransitionAction(PROJECT_VIEW_EDIT)
                        .withRoleTransitions(PROJECT_MANAGE_USERS_GROUP));
    }

    public static PrismStateAction projectViewEditApproved() {
        return projectViewEditAbstract()
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_EDIT_AS_USER) //
                .withAssignments(PROJECT_VIEWER_GROUP, PROJECT_VIEW_AS_USER) //
                .withPartnerAssignments(DEPARTMENT_ADMINISTRATOR_GROUP, PROJECT_VIEW_AS_USER) //
                .withTransitions(PROJECT_VIEW_EDIT_TRANSITION //
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
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST));
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

    private static PrismStateAction projectViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_VIEW_EDIT);
    }

}
