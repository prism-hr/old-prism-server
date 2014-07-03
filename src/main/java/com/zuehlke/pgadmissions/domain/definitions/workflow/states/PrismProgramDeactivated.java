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

public class PrismProgramDeactivated extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CONCLUDE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DEACTIVATED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONCLUDE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONCLUDE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE,  //
                                PrismAction.PROJECT_TERMINATE))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CONFIGURE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withPostComment(true) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONFIGURE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_CONFIGURED_OUTCOME)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_APPROVER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_APPROVER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_APPROVER) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_VIEWER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_VIEWER) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DEACTIVATED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONFIGURE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_CONFIGURED_OUTCOME)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_APPROVER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_APPROVER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_APPROVER) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_VIEWER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_VIEWER) //
                                .withTransitionType(PrismRoleTransitionType.REMOVE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false))), // 
                new PrismStateTransition() // 
                    .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) // 
                    .withTransitionAction(PrismAction.PROGRAM_CONFIGURE) // 
                    .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_CONFIGURED_OUTCOME)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CREATE_PROJECT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
            .withTransitions(Arrays.asList( //
                new PrismStateTransition() // 
                    .withTransitionState(PrismState.PROJECT_APPROVAL) // 
                    .withTransitionAction(PrismAction.PROGRAM_CREATE_PROJECT) //
                    .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_CREATED_OUTCOME)
                    .withRoleTransitions(Arrays.asList( // 
                        new PrismRoleTransition() //
                            .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                            .withTransitionType(PrismRoleTransitionType.CREATE) //
                            .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                            .withRestrictToOwner(false) //
                            .withMaximumPermitted(1), // 
                        new PrismRoleTransition() //
                            .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                            .withTransitionType(PrismRoleTransitionType.CREATE) //
                            .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                            .withRestrictToOwner(false) //
                            .withMinimumPermitted(1) //
                            .withMaximumPermitted(1), // 
                        new PrismRoleTransition() //
                            .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                            .withTransitionType(PrismRoleTransitionType.CREATE) //
                            .withTransitionRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                            .withRestrictToOwner(false) //
                            .withMaximumPermitted(1))),
                new PrismStateTransition() // 
                    .withTransitionState(PrismState.PROJECT_APPROVED) // 
                    .withTransitionAction(PrismAction.PROGRAM_CREATE_PROJECT) //
                    .withTransitionEvaluation(PrismTransitionEvaluation.PROJECT_CREATED_OUTCOME)
                    .withRoleTransitions(Arrays.asList( // 
                        new PrismRoleTransition() //
                            .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                            .withTransitionType(PrismRoleTransitionType.CREATE) //
                            .withTransitionRole(PrismRole.PROJECT_ADMINISTRATOR) //
                            .withRestrictToOwner(false) //
                            .withMaximumPermitted(1), // 
                        new PrismRoleTransition() //
                            .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                            .withTransitionType(PrismRoleTransitionType.CREATE) //
                            .withTransitionRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                            .withRestrictToOwner(false) //
                            .withMinimumPermitted(1) //
                            .withMaximumPermitted(1), // 
                        new PrismRoleTransition() //
                            .withRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                            .withTransitionType(PrismRoleTransitionType.CREATE) //
                            .withTransitionRole(PrismRole.PROJECT_SECONDARY_SUPERVISOR) //
                            .withRestrictToOwner(false) //
                            .withMaximumPermitted(1)))))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(true) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_EXPIRED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.PROJECT_SUSPEND)), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE) // 
                        .withTransitionEvaluation(PrismTransitionEvaluation.PROGRAM_EXPIRED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.PROJECT_SUSPEND))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_EXPORT_APPLICATIONS) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withPostComment(false) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //
    
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
