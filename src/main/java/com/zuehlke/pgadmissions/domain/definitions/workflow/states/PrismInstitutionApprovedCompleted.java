package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismInstitutionApprovedCompleted extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.INSTITUTION_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.INSTITUTION_VIEW_EDIT_AS_USER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMITTER) //
                        .withActionEnhancement(PrismActionEnhancement.INSTITUTION_VIEW_AS_USER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.INSTITUTION_VIEW_EDIT_AS_USER))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_APPROVED_COMPLETED) //
                        .withTransitionAction(PrismAction.INSTITUTION_VIEW_EDIT)
                        .withRoleTransitions(Arrays.asList( //
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMITTER) //
                                .withTransitionType(PrismRoleTransitionType.CREATE) //
                                .withTransitionRole(PrismRole.INSTITUTION_ADMITTER) //
                                .withRestrictToOwner(false),
                            new PrismRoleTransition() //
                                .withRole(PrismRole.INSTITUTION_ADMITTER) //
                                .withTransitionType(PrismRoleTransitionType.DELETE) //
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
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME)
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
                        .withTransitionAction(PrismAction.PROGRAM_VIEW_EDIT) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.PROGRAM_CREATED_OUTCOME) //
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
    }

}
