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

public class PrismProjectDisabledPendingProgramReactivation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_CONCLUDE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_PENDING_PROGRAM_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROJECT_CONCLUDE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)))); //
       
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROJECT_ESCALATE)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_RESTORE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_APPROVAL) // 
                        .withTransitionAction(PrismAction.PROJECT_RESTORE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_RESTORED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_APPROVAL_PENDING_CORRECTION) // 
                        .withTransitionAction(PrismAction.PROJECT_RESTORE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_RESTORED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_APPROVED) // 
                        .withTransitionAction(PrismAction.PROJECT_RESTORE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_RESTORED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DEACTIVATED) // 
                        .withTransitionAction(PrismAction.PROJECT_RESTORE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_RESTORED_OUTCOME)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_TERMINATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) // 
                        .withDefinition(PrismNotificationDefinition.SYSTEM_PROJECT_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROJECT_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROJECT_TERMINATE)))); //
        
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROJECT_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withActionEnhancement(PrismActionEnhancement.PROJECT_VIEW_AS_USER)
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR)))); //
    }

}
