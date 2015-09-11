package com.zuehlke.pgadmissions.domain.definitions.workflow.resume;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.RESUME_ASSIGN_REVIEWERS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.RESUME_EMAIL_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.RESUME_PROVIDE_REVIEW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.RESUME_RETIRE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.RESUME_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_RESUME_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.RESUME_VIEW_AS_RECUITER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement.RESUME_VIEW_EDIT_AS_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.RESUME_CREATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.RESUME_REVIEWER;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.RESUME_ASSIGN_REVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.RESUME_DELETE_REVIEWER_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionGroup.RESUME_PROVIDE_REVIEW_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.RESUME_RETIRED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransitionGroup.RESUME_VIEW_EDIT_TRANSITION;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateTransition;

public class PrismResumeCompleted {

    public static List<PrismStateAction> resumeStateActions(PrismState state) {
        return Lists.newArrayList(
                resumeAssignReviewers(state),
                resumeEmailCreator(),
                resumeProvideReview(state),
                resumeRetire(),
                resumeViewEdit());
    }

    private static PrismStateAction resumeAssignReviewers(PrismState state) {
        return new PrismStateAction() //
                .withAction(RESUME_ASSIGN_REVIEWERS) //
                .withAssignments(RESUME_CREATOR) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(SYSTEM_VIEW_RESUME_LIST) //
                        .withRoleTransitions(RESUME_ASSIGN_REVIEWER_GROUP));
    }

    private static PrismStateAction resumeEmailCreator() {
        return new PrismStateAction() //
                .withAction(RESUME_EMAIL_CREATOR) //
                .withAssignments(RESUME_REVIEWER);
    }

    private static PrismStateAction resumeProvideReview(PrismState state) {
        return new PrismStateAction() //
                .withAction(RESUME_PROVIDE_REVIEW) //
                .withRaisesUrgentFlag() //
                .withAssignments(RESUME_REVIEWER) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(state) //
                        .withTransitionAction(SYSTEM_VIEW_RESUME_LIST) //
                        .withRoleTransitions(RESUME_PROVIDE_REVIEW_GROUP));
    }

    private static PrismStateAction resumeRetire() {
        return new PrismStateAction() //
                .withAction(RESUME_RETIRE) //
                .withAssignments(RESUME_REVIEWER) //
                .withTransitions(new PrismStateTransition() //
                        .withTransitionState(RESUME_RETIRED) //
                        .withTransitionAction(RESUME_RETIRE) //
                        .withRoleTransitions(RESUME_DELETE_REVIEWER_GROUP));
    }

    private static PrismStateAction resumeViewEdit() {
        return new PrismStateAction() //
                .withAction(RESUME_VIEW_EDIT) //
                .withAssignments(RESUME_CREATOR, RESUME_VIEW_EDIT_AS_CREATOR) //
                .withAssignments(RESUME_REVIEWER, RESUME_VIEW_AS_RECUITER) //
                .withTransitions(RESUME_VIEW_EDIT_TRANSITION);
    }

}
