package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;

public class PrismProjectApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_COMPLETE_APPROVAL_STAGE) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withPostComment(true) //
            .withNotificationTemplate(PrismNotificationTemplate.PROJECT_TASK_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION),
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION),
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_COMPLETE_APPROVAL_STAGE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_APPROVAL_PENDING_CORRECTION) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_APPROVED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_APPROVED) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_APPROVED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_REJECTED) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_APPROVED_OUTCOME)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_REJECTED) // 
                        .withTransitionAction(PrismAction.PROJECT_ESCALATE)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_SUSPEND) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROJECT_SUSPEND)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_TERMINATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withTemplate(PrismNotificationTemplate.PROJECT_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_WITHDRAWN) // 
                        .withTransitionAction(PrismAction.PROJECT_TERMINATE)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_VIEW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR),
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_WITHDRAWN) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST))));
    }

}
