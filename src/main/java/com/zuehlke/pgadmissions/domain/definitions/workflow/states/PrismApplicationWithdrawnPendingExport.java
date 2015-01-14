package com.zuehlke.pgadmissions.domain.definitions.workflow.states;

import java.util.Arrays;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionAssignment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateActionNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation;

public class PrismApplicationWithdrawnPendingExport extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_COMMENT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( //
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
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR))) //
                .withNotifications(Arrays.asList( //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION), //
                    new PrismStateActionNotification() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withDefinition(PrismNotificationDefinition.SYSTEM_APPLICATION_UPDATE_NOTIFICATION))) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT) //
                        .withTransitionAction(PrismAction.APPLICATION_COMMENT)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_EMAIL_CREATOR) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withAssignments(Arrays.asList( //
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
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_ESCALATE) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED) //
                        .withTransitionAction(PrismAction.APPLICATION_ESCALATE)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_EXPORT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(false) //
                .withTransitions(Arrays.asList( //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_PENDING_EXPORT) //
                        .withTransitionAction(PrismAction.APPLICATION_EXPORT) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_EXPORTED_OUTCOME), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_COMPLETED) //
                        .withTransitionAction(PrismAction.APPLICATION_EXPORT) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_EXPORTED_OUTCOME), //
                    new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_WITHDRAWN_PENDING_CORRECTION) //
                        .withTransitionAction(PrismAction.APPLICATION_EXPORT) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_EXPORTED_OUTCOME)))); //

        stateActions.add(new PrismStateAction() //
            .withAction(PrismAction.APPLICATION_VIEW_EDIT) //
            .withRaisesUrgentFlag(false) //
            .withDefaultAction(true) //
                .withAssignments(Arrays.asList( //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_CREATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_VIEWER_RECRUITER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.APPLICATION_VIEWER_REFEREE) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_REFEREE), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_ADMITTER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.INSTITUTION_ADMITTER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_ADMITTER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_APPROVER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROGRAM_VIEWER) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_ADMINISTRATOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER), //
                    new PrismStateActionAssignment() //
                        .withRole(PrismRole.PROJECT_PRIMARY_SUPERVISOR) //
                        .withActionEnhancement(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER)))); //
    }

}
