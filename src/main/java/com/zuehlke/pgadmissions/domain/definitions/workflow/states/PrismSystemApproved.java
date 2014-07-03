package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
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
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.SYSTEM_APPROVED) // 
                        .withTransitionAction(PrismAction.SYSTEM_CONFIGURE)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                                .withRestrictToOwner(false)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_CREATE_INSTITUTION) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_APPROVED) // 
                        .withTransitionAction(PrismAction.SYSTEM_CREATE_INSTITUTION)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withRestrictToOwner(true) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_EXPORT_APPLICATIONS) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_EXPORT_PROGRAMS) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_MANAGE_ACCOUNT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false));
    }

}
