package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_REVIEW_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationTerminateSubmitted;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReview extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_ASSIGN_REVIEWERS) //
                .withRaisesUrgentFlag() //
                .withAssignments(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(PrismState.APPLICATION_REVIEW_PENDING_FEEDBACK) //
                        .withTransitionAction(PrismAction.APPLICATION_PROVIDE_REVIEW) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_CREATE_REVIEWER_GROUP)));

        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter());

        stateActions.add(applicationCompleteState(PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE, state, //
                PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP));

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter());

        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(applicationTerminateSubmitted(APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP));

        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter(state)); //

        stateActions.add(applicationWithdrawSubmitted(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                APPLICATION_TERMINATE_REFERENCE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationCompleteReview(PrismState state) {
        return PrismApplicationWorkflow.applicationCompleteState(PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE, state, //
                PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP);
    }

    public static PrismStateAction applicationProvideReview() {
        return new PrismStateAction() //
                .withAction(PrismAction.APPLICATION_PROVIDE_REVIEW) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_PROVIDE_REVIEW_REQUEST)
                .withAssignments(PrismRole.APPLICATION_REVIEWER);
    }

    public static PrismStateAction applicationTerminateReview() {
        return applicationTerminateSubmitted(APPLICATION_TERMINATE_REFERENCE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP);
    }

    public static PrismStateAction applicationViewEditReview(PrismState state) {
        return PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter(state) //
                .withAssignments(PrismRole.APPLICATION_REVIEWER, PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawReview() {
        return applicationWithdrawSubmitted(PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP, //
                APPLICATION_TERMINATE_REFERENCE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP,
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP);
    }

}
