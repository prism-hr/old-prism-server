package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.APPLICATION_COMPLETE_REVIEW_STAGE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REFEREE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_RETIRE_REVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationCompleteReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationProvideReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationTerminateReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationViewEditReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationWithdrawReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiter;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReviewPendingCompletion extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiter()); //

        stateActions.add(applicationCompleteReview(state) //
                .withRaisesUrgentFlag()); //

        stateActions.add(applicationEmailCreatorWithViewerRecruiter()); //

        stateActions.add(applicationEscalate(APPLICATION_RETIRE_REFEREE_GROUP, //
                APPLICATION_RETIRE_REVIEWER_GROUP)); //

        stateActions.add(applicationProvideReview() //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(APPLICATION_REVIEW_PENDING_COMPLETION) //
                        .withTransitionAction(APPLICATION_COMPLETE_REVIEW_STAGE) //
                        .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP))); //

        stateActions.add(applicationTerminateReview());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReview(state)); //
        stateActions.add(applicationWithdrawReview());
    }

}
