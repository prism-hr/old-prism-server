package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_INTERVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_INTERVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_CONFIRMED_INTERVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_SCHEDULED_INTERVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_REVIVE_SCHEDULED_INTERVIEWEE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_REVIVE_SCHEDULED_INTERVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_INTERVIEW_PENDING_SCHEDULING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationUpdateInterviewAvailability;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationInterview.applicationViewEditInterviewScheduled;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationInterviewPendingInterview extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //

        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_INTERVIEW_STAGE, state, //
                APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, //
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, //
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));

        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY) //
                .withRaisesUrgentFlag()
                .withNotification(APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_REQUEST) //
                .withAssignments(APPLICATION_SCHEDULED_INTERVIEW_GROUP) //
                .withNotifications(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_NOTIFICATION) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                        .withRoleTransitions(APPLICATION_CONFIRM_INTERVIEW_AVAILABILITY_GROUP)));

        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //
        stateActions.add(applicationEscalate(APPLICATION_INTERVIEW_PENDING_FEEDBACK, //
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP,
                APPLICATION_UPDATE_SCHEDULED_INTERVIEWER_GROUP_CONFIRMED)); //

        stateActions.add(applicationUpdateInterviewAvailability(APPLICATION_CONFIRMED_INTERVIEW_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_INTERVIEW) //
                        .withTransitionAction(APPLICATION_ASSIGN_INTERVIEWERS) //
                        .withStateTransitionEvaluation(APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME)
                        .withRoleTransitions(APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, //
                                APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                                APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, //
                                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP),
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_INTERVIEW_PENDING_SCHEDULING) //
                                .withTransitionAction(APPLICATION_CONFIRM_INTERVIEW_ARRANGEMENTS) //
                                .withStateTransitionEvaluation(APPLICATION_UPDATED_INTERVIEW_AVAILABILITY_OUTCOME) //
                                .withRoleTransitions(APPLICATION_REVIVE_SCHEDULED_INTERVIEWEE_GROUP, //
                                        APPLICATION_REVIVE_SCHEDULED_INTERVIEWER_GROUP)));

        stateActions.add(applicationTerminateSubmitted(APPLICATION_TERMINATE_REFERENCE_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, //
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, //
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditInterviewScheduled(state)); //

        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                APPLICATION_TERMINATE_REFERENCE_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_SCHEDULED_INTERVIEWEE_GROUP, //
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWEE_GROUP, //
                APPLICATION_RETIRE_SCHEDULED_INTERVIEWER_GROUP, //
                APPLICATION_RETIRE_CONFIRMED_INTERVIEWER_GROUP));
    }

}
