package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_SCHEDULED_INTERVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationCompleteInterviewScheduling;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationConfirmInterviewArrangements;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationProvideInterviewAvailability;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationUpdateInterviewAvailability;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationViewEditInterviewScheduling;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationWithdrawInterviewScheduling;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterviewPendingScheduling extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //
        stateActions.add(applicationCompleteInterviewScheduling(state)); //

        stateActions.add(applicationConfirmInterviewArrangements() //
                .withRaisesUrgentFlag()); //

        stateActions.add(applicationEmailCreatorViewerRecruiter()); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, APPLICATION_RETIRE_INTERVIEWEE_GROUP,
                APPLICATION_RETIRE_INTERVIEWER_GROUP));

        stateActions.add(applicationProvideInterviewAvailability() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                        .withStateTransitionNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION) //
                        .withRoleTransitions(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_GROUP)));

        stateActions.add(applicationUpdateInterviewAvailability(APPLICATION_SCHEDULED_INTERVIEW_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS)));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduling(state)); //
        stateActions.add(applicationWithdrawInterviewScheduling());
    }

}
