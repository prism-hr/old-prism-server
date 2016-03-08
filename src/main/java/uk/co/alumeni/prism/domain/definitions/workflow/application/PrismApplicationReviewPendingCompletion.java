package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_REVIEW_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationCompleteReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationProvideReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationSendMessageReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationViewEditReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationWithdrawReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReviewPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //

        stateActions.add(applicationCompleteReview(state) //
                .withRaisesUrgentFlag() //
                .withNotification(APPLICATION_COMPLETE_REVIEW_STAGE_REQUEST)); //

        stateActions.add(applicationSendMessageReview()); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_REVIEWER_GROUP)); //

        stateActions.add(applicationProvideReview() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
                        .withTransitionAction(APPLICATION_COMPLETE_REVIEW_STAGE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP))); //

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReview(state)); //
        stateActions.add(applicationWithdrawReview());
    }

}
