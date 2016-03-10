package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_REVIEWERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_PROVIDE_REVIEW;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_PROVIDE_REVIEW_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_REVIEWER;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleGroup.APPLICATION_PARENT_ADMINISTRATOR_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTerminationGroup.APPLICATION_TERMINATE_REFERENCE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_ASSIGN_REVIEWERS_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCompleteState;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationSendMessageViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationWithdrawSubmitted;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReview extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(new PrismStateAction() //
                .withAction(APPLICATION_ASSIGN_REVIEWERS) //
                .withRaisesUrgentFlag() //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateTransitions(APPLICATION_ASSIGN_REVIEWERS_TRANSITION));

        stateActions.add(applicationCommentViewerRecruiter());
        stateActions.add(applicationCompleteState(APPLICATION_COMPLETE_REVIEW_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP));
        stateActions.add(applicationSendMessageViewerRecruiter());
        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP));
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditWithViewerRecruiter(state));
        stateActions.add(applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP,
                APPLICATION_RETIRE_REFEREE_GROUP));
    }

    public static PrismStateAction applicationCompleteReview(PrismState state) {
        return applicationCompleteState(APPLICATION_COMPLETE_REVIEW_STAGE, state, APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_RETIRE_REVIEWER_GROUP);
    }

    public static PrismStateAction applicationProvideReview() {
        return new PrismStateAction() //
                .withAction(APPLICATION_PROVIDE_REVIEW) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_PROVIDE_REVIEW_REQUEST)
                .withStateActionAssignments(APPLICATION_REVIEWER);
    }

    public static PrismStateAction applicationSendMessageReview() {
        return applicationSendMessageViewerRecruiter() //
                .withStateActionAssignment(APPLICATION_REVIEWER, APPLICATION_PARENT_ADMINISTRATOR_GROUP) //
                .withStateActionAssignments(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_REVIEWER);
    }

    public static PrismStateAction applicationViewEditReview(PrismState state) {
        return PrismApplicationWorkflow.applicationViewEditWithViewerRecruiter(state) //
                .withStateActionAssignments(APPLICATION_REVIEWER, APPLICATION_VIEW_AS_RECRUITER);
    }

    public static PrismStateAction applicationWithdrawReview() {
        return applicationWithdrawSubmitted(APPLICATION_PARENT_ADMINISTRATOR_GROUP, APPLICATION_TERMINATE_REFERENCE_GROUP, APPLICATION_RETIRE_REFEREE_GROUP,
                APPLICATION_RETIRE_REVIEWER_GROUP);
    }

}
