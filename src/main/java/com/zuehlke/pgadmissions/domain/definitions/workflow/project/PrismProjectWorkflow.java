package com.zuehlke.pgadmissions.domain.definitions.workflow.project;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROJECT_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROJECT_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROJECT_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROJECT_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROJECT_VIEW_EDIT_TRANSITION;

public class PrismProjectWorkflow {
    
    public static PrismStateAction projectEmailCreator() {
        return new PrismStateAction() //
                .withAction(PROJECT_EMAIL_CREATOR) //
                .withAssignments(PROJECT_PARENT_ADMINISTRATOR_GROUP) //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR);
    }
    
    public static PrismStateAction projectEscalateUnapproved() {
        return projectEscalateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_REJECTED) //
                        .withTransitionAction(PROJECT_ESCALATE));
    }
    
    public static PrismStateAction projectEscalateApproved() {
        return projectEscalateAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_DISABLED_COMPLETED) //
                        .withTransitionAction(PROJECT_ESCALATE));
    }
    
    public static PrismStateAction projectSuspendUnapproved() {
        return projectSuspendAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_REJECTED) //
                        .withTransitionAction(PROJECT_SUSPEND));
    }
    
    public static PrismStateAction projectSuspendApproved() {
        return projectSuspendAbstract()
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_DISABLED_PENDING_REACTIVATION) //
                        .withTransitionAction(PROJECT_SUSPEND));
    }
    
    public static PrismStateAction projectDeactivate() {
        return new PrismStateAction() //
                .withAction(PROJECT_DEACTIVATE) //
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_DISABLED_PENDING_REACTIVATION) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST));
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
    
    public static PrismStateAction projectViewEditUnapproved() {
        return projectViewEditAbstract()
                .withActionEnhancement(PROJECT_VIEW_AS_USER) //
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP) //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR);
    }
    
    public static PrismStateAction projectViewEditApproved() {
        return projectViewEditAbstract()
                .withAssignments(PROJECT_ADMINISTRATOR_GROUP, PROJECT_VIEW_EDIT_AS_USER) //
                .withPartnerAssignments(INSTITUTION_ADMINISTRATOR, PROJECT_VIEW_EDIT_AS_USER) //
                .withTransitions(PROJECT_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PROJECT_MANAGE_USERS_GROUP));
    }
    
    public static PrismStateAction projectWithdraw() {
        return new PrismStateAction() //
                .withAction(PROJECT_WITHDRAW) //
                .withAssignments(PROJECT_ADMINISTRATOR)
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROJECT_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_PROJECT_LIST));
    }
    
    private static PrismStateAction projectEscalateAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_ESCALATE);
    }
    
    private static PrismStateAction projectSuspendAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_SUSPEND);
    }
    
    private static PrismStateAction projectTerminateAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_TERMINATE) //
                .withNotifications(PROJECT_ADMINISTRATOR_GROUP, SYSTEM_PROJECT_UPDATE_NOTIFICATION);
    }
    
    private static PrismStateAction projectViewEditAbstract() {
        return new PrismStateAction() //
                .withAction(PROJECT_VIEW_EDIT);
    }
    
}
