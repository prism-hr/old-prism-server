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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;

public class PrismInstitutionApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_CONFIGURE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMITTER), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.INSTITUTION_APPROVED) // 
                        .withTransitionAction(PrismAction.INSTITUTION_CONFIGURE)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMITTER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMITTER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMITTER) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMITTER) //
                                .withRestrictToOwner(false)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_CREATE_PROGRAM) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVAL) // 
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_CREATED_OUTCOME)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(true) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1))), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVED) // 
                        .withTransitionAction(PrismAction.INSTITUTION_CREATE_PROGRAM) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_CREATED_OUTCOME) // 
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(true) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1)))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_IMPORT_PROGRAM) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVED) // 
                        .withTransitionAction(PrismAction.INSTITUTION_IMPORT_PROGRAM)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(true) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1))))));
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_VIEW) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_VIEW_APPLICATION_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_VIEW_PROGRAM_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_VIEW_PROJECT_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)); //
    }

}
