package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionEvaluation;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterviewPendingInterview extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //

        stateActions.add(PrismApplicationWorkflow.applicationCompleteState(PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE, state, //
                PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));

        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY) //
                .withRaisesUrgentFlag()
                .withNotification(PrismNotificationDefinition.APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_REQUEST) //
                .withAssignments(PrismRoleGroup.APPLICATION_SCHEDULED_INTERVIEW_GROUP) //
                .withNotifications(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP, PrismNotificationDefinition.APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(PrismAction.SYSTEM_VIEW_APPLICATION_LIST) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_GROUP)));

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED)); //

        stateActions.add(PrismApplicationInterview.applicationUpdateInterviewAvailability(PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_INTERVIEW) //
                        .withTransitionAction(PrismAction.APPLICATION_ASSIGN_INTERVIEWERS) //
                        .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME)
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, //
                                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                                PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, //
                                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP),
                        new PrismStateTransition() //
                                .withTransitionState(PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                                .withTransitionAction(PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                                .withStateTransitionEvaluation(PrismStateTransitionEvaluation.APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME) //
                                .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_REVIVE_SCHEDULED_INTERVIEWEE_GROUP, //
                                        PrismRoleTransitionGroup.APPLICATION_REVIVE_SCHEDULED_INTERVIEWER_GROUP)));

        stateActions.add(PrismApplicationWorkflow.applicationTerminateSubmitted(PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));

        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationInterview.applicationViewEditInterviewScheduled(state)); //

        stateActions.add(PrismApplicationWorkflow.applicationWithdrawSubmitted(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));
    }

}
