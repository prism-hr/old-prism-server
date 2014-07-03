package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismProgramApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CORRECT) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(true) //
            .withPostComment(true) //
            .withNotificationTemplate(PrismNotificationTemplate.PROGRAM_TASK_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVAL) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_WITHDRAWN) // 
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_WITHDRAWN) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST))));
    }

}
