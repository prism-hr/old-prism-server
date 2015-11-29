package uk.co.alumeni.prism.domain.definitions.workflow.application;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReviewPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter());
        stateActions.add(PrismApplicationReview.applicationCompleteReview(state));
        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter());
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismState.APPLICATION_REVIEW_PENDING_COMPLETION));

        stateActions.add(PrismApplicationReview.applicationProvideReview()
                .withStateTransitions(PrismStateTransitionGroup.APPLICATION_PROVIDE_REVIEW_TRANSITION //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP)));

        stateActions.add(PrismApplicationReview.applicationTerminateReview());
        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationReview.applicationViewEditReview(state)); //
        stateActions.add(PrismApplicationReview.applicationWithdrawReview());
    }

}
