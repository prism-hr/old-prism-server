package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_SCHEDULED_INTERVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationInterviewPendingAvailability extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationCompleteInterviewScheduling(state));
        stateActions.add(applicationConfirmInterviewArrangements()); //
        stateActions.add(applicationSendMessageInterviewScheduling()); //
        stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_SCHEDULING)); //

        stateActions.add(applicationProvideInterviewAvailability() //
                .withStateTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_TRANSITION //
                        .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP))); //

        stateActions.add(applicationUpdateInterviewAvailability(APPLICATION_SCHEDULED_INTERVIEW_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST)
                        .withStateTransitionNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION)));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduling(state)); //
        stateActions.add(applicationWithdrawInterviewScheduling());
    }
}
