package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismSystemApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_CONFIGURE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_CREATE_INSTITUTION) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_APPROVED) // 
                        .withTransitionAction(PrismAction.SYSTEM_CREATE_INSTITUTION) // 
                        .withPostComment(false)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_EXPORT_APPLICATIONS) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_EXPORT_PROGRAMS) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_MANAGE_ACCOUNT) //
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
            .withAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
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
            .withAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST) //
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
            .withAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
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
            .withAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
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
