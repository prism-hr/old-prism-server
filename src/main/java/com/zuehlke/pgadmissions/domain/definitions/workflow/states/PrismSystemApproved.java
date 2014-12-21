package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismSystemApproved extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
            .withActionEnhancement(PrismActionEnhancement.SYSTEM_VIEW_EDIT_AS_USER)
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.SYSTEM_RUNNING) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_EDIT)
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                                .withRestrictToOwner(false)))))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_CREATE_INSTITUTION) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) // //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_APPROVAL) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_CREATED_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withRestrictToOwner(true) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1))),
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_APPROVED) //
                        .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.INSTITUTION_CREATED_OUTCOME) //
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withRestrictToOwner(true) //
                                .withMinimumPermitted(1) //
                                .withMaximumPermitted(1)))))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_MANAGE_ACCOUNT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)); //

        stateActions.add(new PrismStateAction() //
             .withAction(PrismAction.SYSTEM_STARTUP) //
             .withRaisesUrgentFlag(false) //
             .withDefaultAction(false) //
             .withTransitions(Arrays.asList( //
                 new PrismStateTransition() //
                     .withTransitionState(PrismState.SYSTEM_RUNNING) //
                     .withTransitionAction(PrismAction.SYSTEM_VIEW_EDIT)
                     .withRoleTransitions(Arrays.asList( //
                         new PrismRoleTransition() //
                             .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                             .withTransitionType(PrismRoleTransitionType.CREATE) //
                             .withTransitionRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                             .withRestrictToOwner(true) //
                             .withMinimumPermitted(1) //
                             .withMaximumPermitted(1))))));

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_INSTITUTION_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_PROGRAM_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false)); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.SYSTEM_VIEW_PROJECT_LIST) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false));
    }

}
