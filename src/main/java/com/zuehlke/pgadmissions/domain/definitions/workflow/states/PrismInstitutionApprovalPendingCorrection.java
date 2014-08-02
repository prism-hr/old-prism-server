package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismInstitutionApprovalPendingCorrection extends PrismWorkflowState {

    @Override
    protected void setStateActions() {    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_CORRECT) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(true) //
            .withNotificationTemplate(PrismNotificationTemplate.INSTITUTION_TASK_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_APPROVAL) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_WITHDRAWN) // 
                        .withTransitionAction(PrismAction.INSTITUTION_ESCALATE)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withActionEnhancement(PrismActionEnhancement.INSTITUTION_VIEW_AS_USER) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_WITHDRAW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_WITHDRAWN) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST))));
    }

}