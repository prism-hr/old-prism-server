package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import com.zuehlke.pgadmissions.domain.definitions.workflow.*;

import java.util.Arrays;

public class PrismInstitutionApproved extends PrismWorkflowState {

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
            .withActionEnhancement(PrismActionEnhancement.INSTITUTION_VIEW_AS_USER) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMITTER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.SYSTEM_ADMINISTRATOR))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_APPROVED) //
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
            .withAction(PrismAction.INSTITUTION_STARTUP) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.INSTITUTION_APPROVED_COMPLETED) //
                        .withTransitionAction(PrismAction.INSTITUTION_STARTUP)))
                .withNotifications(Arrays.asList( //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.INSTITUTION_STARTUP_NOTIFICATION)))); //
    }

}
