package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismProgramDeactivated extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CONCLUDE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DEACTIVATED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONCLUDE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) // 
                        .withTransitionAction(PrismAction.PROGRAM_CONCLUDE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_RECRUITED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.APPLICATION_TERMINATE,  //
                                PrismAction.PROJECT_TERMINATE))))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withActionEnhancement(PrismActionEnhancement.PROGRAM_VIEW_EDIT_AS_USER) //
                .withAssignments(Arrays.asList( // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), // 
                    new PrismStateActionAssignment() // 
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_APPROVED) // 
                        .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_VIEW_EDIT_OUTCOME)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_APPROVER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_APPROVER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_APPROVER) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_VIEWER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_VIEWER) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false))), //
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DEACTIVATED) // 
                        .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_VIEW_EDIT_OUTCOME)
                        .withRoleTransitions(Arrays.asList( // 
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_APPROVER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_APPROVER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_APPROVER) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_VIEWER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.PROGRAM_VIEWER) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.PROGRAM_VIEWER) //
                                .withRestrictToOwner(false))), // 
                new PrismStateTransition() // 
                    .withTransitionState(PrismState.PROGRAM_DISABLED_COMPLETED) // 
                    .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT) // 
                    .withTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_VIEW_EDIT_OUTCOME)))); //
    
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.PROGRAM_CREATE_PROJECT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
            .withTransitions(Arrays.asList( //
                new PrismStateTransition() // 
                    .withTransitionState(PrismState.PROJECT_APPROVAL) // 
                    .withTransitionAction(PrismAction.PROGRAM_CREATE_PROJECT) //
                    .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME)
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
                    .withTransitionEvaluation(PrismStateTransitionEvaluation.PROJECT_CREATED_OUTCOME)
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
                .withTransitions(Arrays.asList( // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_PENDING_IMPORT_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_EXPIRED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.PROJECT_SUSPEND)), // 
                    new PrismStateTransition() // 
                        .withTransitionState(PrismState.PROGRAM_DISABLED_PENDING_REACTIVATION) // 
                        .withTransitionAction(PrismAction.PROGRAM_ESCALATE) // 
                        .withTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_EXPIRED_OUTCOME)// 
                        .withPropagatedActions(Arrays.asList( //
                                PrismAction.PROJECT_SUSPEND))))); //
    }

}