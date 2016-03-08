package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_REVIEW_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationCompleteReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationProvideReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationSendMessageReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationTerminateReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationViewEditReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationWithdrawReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReviewPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter());
        stateActions.add(applicationCompleteReview(state));
        stateActions.add(applicationSendMessageReview());
        stateActions.add(applicationEscalate(APPLICATION_REVIEW_PENDING_COMPLETION));

        stateActions.add(applicationProvideReview()
                .withStateTransitions(APPLICATION_PROVIDE_REVIEW_TRANSITION //
                        .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP)));

        stateActions.add(applicationTerminateReview());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReview(state)); //
        stateActions.add(applicationWithdrawReview());
    }

}
