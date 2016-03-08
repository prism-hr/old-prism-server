package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_INTERVIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_POTENTIAL_INTERVIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_POTENTIAL_INTERVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_INTERVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_UPDATE_POTENTIAL_INTERVIEWEE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_CONFIRMED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_SCHEDULED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_INTERVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_CONFIRMED_INTERVIEW_OUTCOME;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ASSIGN_INTERVIEWERS_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterview extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_ASSIGN_INTERVIEWERS) //
                .withRaisesUrgentFlag() //
                .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(APPLICATION_ASSIGN_INTERVIEWERS_TRANSITION)); //

        stateActions.add(applicationCommentWithViewerRecruiter()); //
        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP)); //
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP));
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiter(state)); //
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
                .withAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_INTERVIEW) //
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS) //
                        .withStateTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
                        .withRoleTransitions(APPLICATION_RETIRE_INTERVIEWEE_GROUP, APPLICATION_RETIRE_INTERVIEWER_GROUP),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_INTERVIEW_PENDING_INTERVIEW) //
                                .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                                .withStateTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
                                .withRoleTransitions(APPLICATION_UPDATE_POTENTIAL_INTERVIEWEE_GROUP, APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_SCHEDULED), //
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_INTERVIEW_PENDING_FEEDBACK) //
                                .withTransitionAction(PrismAction.APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
                                .withStateTransitionEvaluation(APPLICATION_CONFIRMED_INTERVIEW_OUTCOME) //
                                .withRoleTransitions(APPLICATION_RETIRE_POTENTIAL_INTERVIEWEE_GROUP, APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP,
                                        APPLICATION_UPDATE_POTENTIAL_INTERVIEWER_GROUP_CONFIRMED, APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED));
    }

    public static PrismStateAction applicationProvideInterviewAvailability() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY) //
                .withRaisesUrgentFlag()
                .withNotification(APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REQUEST) //
                .withAssignments(APPLICATION_POTENTIAL_INTERVIEW_GROUP) //
                .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_NOTIFICATION); //
    }

    public static PrismStateAction applicationProvideInterviewFeedback() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_REQUEST) //
                .withAssignments(APPLICATION_INTERVIEWER);
    }

    public static PrismStateAction applicationUpdateInterviewAvailability(PrismRoleGroup assignments) {
        return new PrismStateAction() //
                .withAction(APPLICATION_UPDATE_INTERVIEW_AVAILABILITY) //
                .withAssignments(assignments) //
                .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_UPDATE_INTERVIEW_AVAILABILITY_NOTIFICATION); //
    }

    public static PrismStateAction applicationViewEditInterviewScheduling(PrismState state) {
        return applicationViewEditInterviewScheduled(state) //
                .withAssignments(APPLICATION_POTENTIAL_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationViewEditInterviewScheduled(PrismState state) {
        return applicationViewEditWithViewerRecruiter(state) //
                .withAssignments(APPLICATION_INTERVIEWER, APPLICATION_VIEW_AS_RECRUITER);
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
