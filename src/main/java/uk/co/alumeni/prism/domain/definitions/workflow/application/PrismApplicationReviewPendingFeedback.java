package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_REVIEW_TRANSITION;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReviewPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter());
        stateActions.add(PrismApplicationReview.applicationCompleteReview(state));
        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter());
        stateActions.add(PrismApplicationWorkflow.applicationEscalate(APPLICATION_REVIEW_PENDING_COMPLETION));

        stateActions.add(PrismApplicationReview.applicationProvideReview()
                .withStateTransitions(APPLICATION_PROVIDE_REVIEW_TRANSITION //
                        .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP)));

        stateActions.add(PrismApplicationReview.applicationTerminateReview());
        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationReview.applicationViewEditReview(state)); //
        stateActions.add(PrismApplicationReview.applicationWithdrawReview());
    }

}
