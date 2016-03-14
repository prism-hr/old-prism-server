package uk.co.alumeni.prism.domain.definitions.workflow.application;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinition.APPLICATION_COMPLETE_REVIEW_STAGE_REQUEST;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationReview.applicationCompleteReview;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorViewerRecruiter;
import static uk.co.alumeni.prism.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismStateTransition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReviewPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentViewerRecruiter()); //

        stateActions.add(applicationCompleteReview(state) //
                .withRaisesUrgentFlag() //
                .withNotificationDefinition(APPLICATION_COMPLETE_REVIEW_STAGE_REQUEST)); //

        stateActions.add(applicationEmailCreatorViewerRecruiter()); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_REVIEWER_GROUP)); //

        stateActions.add(PrismApplicationReview.applicationProvideReview() //
                .withStateTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
                        .withTransitionAction(APPLICATION_COMPLETE_REVIEW_STAGE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP))); //

        stateActions.add(PrismApplicationWorkflow.applicationUploadReference(state));
        stateActions.add(PrismApplicationReview.applicationViewEditReview(state)); //
        stateActions.add(PrismApplicationReview.applicationWithdrawReview());
    }

}
