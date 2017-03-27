package com.zuehlke.pgadmissions.domain.definitions.workflow.program;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PROGRAM_UPDATE_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.PROGRAM_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.PROGRAM_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.PROGRAM_MANAGE_USERS_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_REJECTED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_WITHDRAWN;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_ESCALATE_APPROVED_TRANSITION_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.PROGRAM_VIEW_EDIT_TRANSITION;

public class PrismProgramWorkflow {
    
    public static PrismStateAction programEmailCreator() {
        return new PrismStateAction() //
                .withAction(PROGRAM_EMAIL_CREATOR) //
                .withAssignments(PROGRAM_PARENT_ADMINISTRATOR_GROUP);
    }
    
    public static PrismStateAction programEscalateUnapproved() {
        return new PrismStateAction() //
                .withAction(PROGRAM_ESCALATE) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_REJECTED) //
                        .withTransitionAction(PROGRAM_ESCALATE));
    }
    
    public static PrismStateAction programEscalateApproved() {
        return new PrismStateAction() //
                .withAction(PROGRAM_ESCALATE) //
                .withNotifications(PROGRAM_ADMINISTRATOR_GROUP, SYSTEM_PROGRAM_UPDATE_NOTIFICATION)
                .withTransitions(PROGRAM_ESCALATE_APPROVED_TRANSITION_GROUP);
    }
    
    public static PrismStateAction programViewEditUnapproved() {
        return new PrismStateAction() //
                .withAction(PROGRAM_VIEW_EDIT) //
                .withActionEnhancement(PROGRAM_VIEW_AS_USER) //
                .withAssignments(PROGRAM_ADMINISTRATOR_GROUP);
    }
    
    public static PrismStateAction programViewEditApproved() {
        return new PrismStateAction() //
                .withAction(PROGRAM_VIEW_EDIT) //
                .withAssignments(PROGRAM_ADMINISTRATOR_GROUP, PROGRAM_VIEW_EDIT_AS_USER) //
                .withNotifications(PROGRAM_ADMINISTRATOR_GROUP, SYSTEM_PROGRAM_UPDATE_NOTIFICATION) //
                .withTransitions(PROGRAM_VIEW_EDIT_TRANSITION //
                        .withRoleTransitions(PROGRAM_MANAGE_USERS_GROUP));
    }
    
    public static PrismStateAction programWithdraw() {
        return new PrismStateAction() //
                .withAction(PROGRAM_WITHDRAW) //
                .withAssignments(PROGRAM_ADMINISTRATOR) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(PROGRAM_WITHDRAWN) //
                        .withTransitionAction(SYSTEM_VIEW_PROGRAM_LIST));
    }
    
}
