package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;

public class PrismProgramDisabledPendingImportReactivation extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CONCLUDE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONCLUDE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE,  //
                                PrismAction.PROJECT_TERMINATE)), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONCLUDE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.PROJECT_SUSPEND))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROGRAM_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROGRAM_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE,  //
                                PrismAction.PROJECT_TERMINATE))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_RESTORE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withPostComment(true) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROGRAM_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROGRAM_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVED) // 
                        .withTransitionAction(PrismAction.PROGRAM_RESTORE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_REACTIVATED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.PROJECT_RESTORE)), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DEACTIVATED) // 
                        .withTransitionAction(PrismAction.PROGRAM_RESTORE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_REACTIVATED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.PROJECT_RESTORE))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withPostComment(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW_APPLICATION_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW_PROJECT_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false));
    }

}