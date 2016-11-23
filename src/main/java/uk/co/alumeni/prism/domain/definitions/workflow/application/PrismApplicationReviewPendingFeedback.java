package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_REVIEW_TRANSITION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.*;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.*;

public class PrismApplicationReviewPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRefereeViewerRecruiter());
        stateActions.add(applicationCompleteReview(state));
        stateActions.add(applicationSendMessageReview());
        stateActions.add(applicationEscalate(APPLICATION_REVIEW_PENDING_COMPLETION));

        stateActions.add(applicationProvideReview()
                .withStateTransitions(APPLICATION_PROVIDE_REVIEW_TRANSITION //
                        .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP)));

        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReview(state)); //
        stateActions.add(applicationWithdrawReview());
    }

}
