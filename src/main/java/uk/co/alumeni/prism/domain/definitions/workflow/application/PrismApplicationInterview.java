package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.*;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_INTERVIEW_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ASSIGN_INTERVIEWERS_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationInterview extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_ASSIGN_INTERVIEWERS) //
                .withRaisesUrgentFlag() //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(APPLICATION_ASSIGN_INTERVIEWERS_TRANSITION)); //

        stateActions.add(applicationCommentViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationSendMessageViewerRefereeViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP)); //
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP));
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditViewerRefereeViewerRecruiter(state)); //
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationCompleteInterviewScheduling(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP,
                APPLICATION_RETIRE_INTERVIEWEE_GROUP,
                APPLICATION_RETIRE_INTERVIEWER_GROUP);
    }

    public static PrismStateAction applicationCompleteInterviewScheduled(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP,
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP);
    }

    public static PrismStateAction applicationConfirmInterviewArrangements() {
        return new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                                .withTransitionState(APPLICATION_INTERVIEW) //
                                .withTransitionAction(APPLICATION_ASSIGN_INTERVIEWERS) //
                                .withStateTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
                                .withRoleTransitions(APPLICATION_RETIRE_INTERVIEWEE_GROUP, APPLICATION_RETIRE_INTERVIEWER_GROUP),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_INTERVIEW_PENDING_INTERVIEW) //
                                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
                                .withRoleTransitions(APPLICATION_UPDATE_POTENTIAL_INTERVIEWEE_GROUP, APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_SCHEDULED), //
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
                                .withTransitionAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
                                .withStateTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
                                .withRoleTransitions(APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP, APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP,
                                        APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_CONFIRMED, APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED));
    }

    public static PrismStateAction applicationProvideInterviewAvailability() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY) //
                .withRaisesUrgentFlag()
                .withNotificationDefinition(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST) //
                .withStateActionAssignments(APPLICATION_POTENTIAL_INTERVIEW_GROUP); //
    }

    public static PrismStateAction applicationProvideInterviewFeedback() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_REQUEST) //
                .withStateActionAssignments(APPLICATION_INTERVIEWER);
    }

    public static PrismStateAction applicationUpdateInterviewAvailability(PrismRoleGroup assignments) {
        return new PrismStateAction() //
                .withAction(APPLICATION_UPDATE_INTERVIEW_AVAILABILITY) //
                .withStateActionAssignments(assignments); //
    }

    public static PrismStateAction applicationSendMessageInterviewScheduling() {
        return applicationSendMessageViewerRefereeViewerRecruiter() //
                .withStateActionAssignments(APPLICATION_POTENTIAL_INTERVIEW_GROUP, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_SCHEDULED_INTERVIEW_GROUP, APPLICATION_PARENT_ADMINISTRATOR_GROUP); //
    }

    public static PrismStateAction applicationSendMessageInterviewFeedback() {
        return applicationSendMessageViewerRefereeViewerRecruiter() //
                .withStateActionAssignment(APPLICATION_INTERVIEWER, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_INTERVIEWER); //
    }

    public static PrismStateAction applicationViewEditInterviewScheduling(PrismState state) {
        return applicationViewEditInterviewScheduled(state) //
                .withStateActionAssignments(APPLICATION_POTENTIAL_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER) //
                .withStateActionAssignments(APPLICATION_SCHEDULED_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationViewEditInterviewScheduled(PrismState state) {
        return applicationViewEditViewerRefereeViewerRecruiter(state) //
                .withStateActionAssignments(APPLICATION_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawInterviewScheduling() {
        return applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_INTERVIEWEE_GROUP, APPLICATION_RETIRE_INTERVIEWER_GROUP);
    }

    public static PrismStateAction applicationWithdrawInterviewScheduled() {
        return applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP);
    }

}
