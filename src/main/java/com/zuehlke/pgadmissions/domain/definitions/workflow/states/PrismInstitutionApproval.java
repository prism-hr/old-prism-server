package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismInstitutionApproval extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_COMPLETE_APPROVAL_STAGE) //
            .withRaisesUrgentFlag(true) //
            .withDefaultAction(false) //
            .withNotificationTemplate(PrismNotificationTemplate.SYSTEM_INSTITUTION_TASK_REQUEST) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR))) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.SYSTEM_INSTITUTION_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.INSTITUTION_COMPLETE_APPROVAL_STAGE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_APPROVAL_PENDING_CORRECTION) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST)
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_APPROVED) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST)
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_REJECTED) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST)
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_APPROVED_OUTCOME)))); //
    
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
                        .withTransitionState(PrismState.INSTITUTION_REJECTED) // 
                        .withTransitionAction(PrismAction.INSTITUTION_ESCALATE)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_VIEW_EDIT) //
            .withActionEnhancement(PrismActionEnhancement.INSTITUTION_VIEW_AS_USER)
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR)))); //
        
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
