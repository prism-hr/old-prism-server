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

public class PrismProgramDisabled extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CONCLUDE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONCLUDE) // 
                        .withEvaluation(PrismTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME) // 
                        .withPostComment(true), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONCLUDE) // 
                        .withEvaluation(PrismTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME) // 
                        .withPostComment(true)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE,  //
                                PrismAction.PROJECT_TERMINATE))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withNotifications(Arrays.asList( // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROGRAM_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROGRAM_UPDATE_NOTIFICATION), // 
                    new PrismStateActionNotification() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR) // 
                        .withTemplate(PrismNotificationTemplate.PROGRAM_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE) // 
                        .withPostComment(true)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE,  //
                                PrismAction.PROJECT_TERMINATE))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_EXPORT_APPLICATIONS) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_RESTORE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVED) // 
                        .withTransitionAction(PrismAction.PROGRAM_RESTORE) // 
                        .withPostComment(true)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_APPROVER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_VIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW_APPLICATION_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_APPROVER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_VIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW_PROJECT_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_CREATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_POTENTIAL_INTERVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_REVIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_SUGGESTED_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_APPROVER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_VIEWER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR))));
    }

}
