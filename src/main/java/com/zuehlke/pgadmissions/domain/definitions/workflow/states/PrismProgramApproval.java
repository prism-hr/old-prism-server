package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismProgramApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_COMPLETE_APPROVAL_STAGE) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotificationTemplate(PrismNotificationDefinition.SYSTEM_PROGRAM_TASK_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROGRAM_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withDefinition(PrismNotificationDefinition.PROGRAM_COMPLETE_APPROVAL_STAGE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVAL_PENDING_CORRECTION) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVED) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_REJECTED) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_APPROVED_OUTCOME)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_REJECTED) // 
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withActionEnhancement(PrismActionEnhancement.PROGRAM_VIEW_AS_USER) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_WITHDRAWN) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST))));
    }

}
