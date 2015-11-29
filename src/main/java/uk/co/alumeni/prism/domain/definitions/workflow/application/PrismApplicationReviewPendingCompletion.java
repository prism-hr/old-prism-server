package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReviewPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(PrismApplicationWorkflow.applicationCommentWithViewerRecruiter()); //

        stateActions.add(PrismApplicationReview.applicationCompleteReview(state) //
                .withRaisesUrgentFlag()); //

        stateActions.add(PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter()); //

        stateActions.add(PrismApplicationWorkflow.applicationEscalate(PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP, //
                PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP)); //

        stateActions.add(PrismApplicationReview.applicationProvideReview() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
                        .withTransitionAction(PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE) //
                        .withRoleTransitions(PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP))); //

        stateActions.add(PrismApplicationReview.applicationTerminateReview());
        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationReview.applicationViewEditReview(state)); //
        stateActions.add(PrismApplicationReview.applicationWithdrawReview());
    }

}
