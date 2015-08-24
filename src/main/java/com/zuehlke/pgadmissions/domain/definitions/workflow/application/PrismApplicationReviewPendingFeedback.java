package com.zuehlke.pgadmissions.domain.definitions.workflow.application;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.APPLICATION_PROVIDE_REVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.APPLICATION_REVIEW_PENDING_COMPLETION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.APPLICATION_PROVIDE_REVIEW_TRANSITION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationCompleteReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationProvideReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationTerminateReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationViewEditReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationReview.applicationWithdrawReview;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationCommentWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEmailCreatorWithViewerRecruiterAndAdministrator;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationEscalate;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.application.PrismApplicationWorkflow.applicationUploadReference;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowState;

public class PrismApplicationReviewPendingFeedback extends PrismWorkflowState {

    @Override
    protected void setStateActions() {
        stateActions.add(applicationCommentWithViewerRecruiterAndAdministrator());
        stateActions.add(applicationCompleteReview(state));
        stateActions.add(applicationEmailCreatorWithViewerRecruiterAndAdministrator());
        stateActions.add(applicationEscalate(APPLICATION_REVIEW_PENDING_COMPLETION));

        stateActions.add(applicationProvideReview()
                .withTransitions(APPLICATION_PROVIDE_REVIEW_TRANSITION //
                        .withRoleTransitions(APPLICATION_PROVIDE_REVIEW_GROUP)));

        stateActions.add(applicationTerminateReview());
        stateActions.add(applicationUploadReference(state));
        stateActions.add(applicationViewEditReview(state)); //
        stateActions.add(applicationWithdrawReview());
    }

}
