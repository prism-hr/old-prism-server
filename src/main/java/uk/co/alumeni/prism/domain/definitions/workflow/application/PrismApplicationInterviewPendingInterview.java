package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationUpdateInterviewAvailability;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationInterview.applicationViewEditInterviewScheduled;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationInterviewPendingInterview extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentViewerRefereeViewerRecruiter()); //

        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP,
                APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP,
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY) //
                .withRaisesUrgentFlag()
                .withNotificationDefinition(APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_REQUEST) //
                .withStateActionAssignments(APPLICATION_SCHEDULED_INTERVIEW_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                        .withStateTransitionNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION) //
                        .withRoleTransitions(APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_GROUP)));

        stateActions.add(applicationSendMessageViewerRefereeViewerRecruiter() //
                .withStateActionAssignments(APPLICATION_SCHEDULED_INTERVIEW_GROUP, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_SCHEDULED_INTERVIEW_GROUP) //
                .withStateActionAssignments(APPLICATION_CONFIRMED_INTERVIEW_GROUP, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_CONFIRMED_INTERVIEW_GROUP)); //

        stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_FEEDBACK, APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP,
                APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED)); //

        stateActions.add(applicationUpdateInterviewAvailability(APPLICATION_CONFIRMED_INTERVIEW_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                                .withTransitionState(APPLICATION_INTERVIEW) //
                                .withTransitionAction(APPLICATION_ASSIGN_INTERVIEWERS) //
                                .withStateTransitionEvaluation(APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME) //
                                .withStateTransitionNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP,
                                        APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION) //
                                .withRoleTransitions(APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                                        APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP), //
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                                .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                                .withStateTransitionEvaluation(APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME) //
                                .withStateTransitionNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP,
                                        APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION) //
                                .withRoleTransitions(APPLICATION_REVIVE_SCHEDULED_INTERVIEWEE_GROUP, APPLICATION_REVIVE_SCHEDULED_INTERVIEWER_GROUP)));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduled(state)); //

        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP, APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP,
                APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));
    }

}
