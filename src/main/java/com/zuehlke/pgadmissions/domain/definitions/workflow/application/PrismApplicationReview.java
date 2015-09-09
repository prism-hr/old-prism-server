package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleGroup.APPLICATION_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_CREATE_REVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_ADMINISTRATOR_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_ALL_STATES_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionEvaluation.APPLICATION_ASSIGNED_REVIEWER_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReview extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_ASSIGN_REVIEWERS) //
                .withRaisesUrgentFlag() //
                .withAssignments(APPLICATION_ADMINISTRATOR_GROUP) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REVIEW) //
                        .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST) //
                        .withTransitionEvaluation(APPLICATION_ASSIGNED_REVIEWER_OUTCOME) //
                        .withRoleTransitions(APPLICATION_CREATE_ADMINISTRATOR_GROUP), //
                        new PrismStateTransition() //
                                .withTransitionState(APPLICATION_REVIEW_PENDING_FEEDBACK) //
                                .withTransitionAction(APPLICATION_PROVIDE_REVIEW) //
                                .withTransitionEvaluation(APPLICATION_ASSIGNED_REVIEWER_OUTCOME) //
                                .withRoleTransitions(APPLICATION_CREATE_REVIEWER_GROUP)));

        stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator());

        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_REVIEW_STAGE, state, //
                APPLICATION_ADMINISTRATOR_GROUP, APPLICATION_RETIRE_ADMINISTRATOR_GROUP));

        stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator());

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP));

        stateActions.add(applicationTerminateSubmitted(APPLICATION_TERMINATE_ALL_STATES_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiterAndAdministrator(state)); //

        stateActions.add(applicationWithdrawSubmitted(APPLICATION_ADMINISTRATOR_GROUP, //
                APPLICATION_TERMINATE_ALL_STATES_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP));
    }

    public static PrismStateAction applicationCompleteReview(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_REVIEW_STAGE, state, //
                APPLICATION_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_REVIEWER_GROUP);
    }

    public static PrismStateAction applicationProvideReview() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_REVIEW) //
                .withRaisesUrgentFlag() //
                .withAssignments(APPLICATION_REVIEWER);
    }

    public static PrismStateAction applicationTerminateReview() {
        return applicationTerminateSubmitted(APPLICATION_TERMINATE_ALL_STATES_GROUP,
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_REVIEWER_GROUP);
    }

    public static PrismStateAction applicationViewEditReview(PrismState state) {
        return applicationViewEditWithViewerRecruiterAndAdministrator(state) //
                .withAssignments(APPLICATION_REVIEWER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawReview() {
        return applicationWithdrawSubmitted(APPLICATION_ADMINISTRATOR_GROUP, //
                APPLICATION_TERMINATE_ALL_STATES_GROUP,
                APPLICATION_RETIRE_ADMINISTRATOR_GROUP, //
                APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_REVIEWER_GROUP);
    }

}
