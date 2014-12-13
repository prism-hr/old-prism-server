package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

public enum PrismStateTransitionEvaluation {

    APPLICATION_COMPLETED_OUTCOME(false, APPLICATION), //
    APPLICATION_SUPERVISION_CONFIRMED_OUTCOME(false, APPLICATION), //
    APPLICATION_STATE_COMPLETED_OUTCOME(true, APPLICATION), //
    APPLICATION_EXPORTED_OUTCOME(false, APPLICATION), //
    APPLICATION_INTERVIEW_RSVPED_OUTCOME(false, APPLICATION), //
    APPLICATION_INTERVIEWED_OUTCOME(false, APPLICATION), //
    APPLICATION_ASSIGNED_RECRUITER_OUTCOME(false, APPLICATION), //
    APPLICATION_PROCESSED_OUTCOME(false, APPLICATION), //
    APPLICATION_VERIFIED_OUTCOME(false, APPLICATION), //
    APPLICATION_REFERENCED_OUTCOME(false, APPLICATION), //
    APPLICATION_VERIFICATION_COMPLETED_OUTCOME(true, APPLICATION), //
    APPLICATION_REFERENCE_COMPLETED_OUTCOME(true, APPLICATION), //
    APPLICATION_REVIEWED_OUTCOME(false, APPLICATION), //
    APPLICATION_RECRUITED_OUTCOME(false, APPLICATION), //
    INSTITUTION_APPROVED_OUTCOME(true, INSTITUTION), //
    INSTITUTION_CREATED_OUTCOME(false, INSTITUTION), //
    PROGRAM_APPROVED_OUTCOME(true, PROGRAM), //
    PROGRAM_VIEW_EDIT_OUTCOME(true, PROGRAM), //
    PROGRAM_CREATED_OUTCOME(false, PROGRAM), //
    PROGRAM_ESCALATED_OUTCOME(false, PROGRAM), //
    PROJECT_APPROVED_OUTCOME(true, PROJECT), //
    PROJECT_VIEW_EDIT_OUTCOME(true, PROJECT), //
    PROJECT_CREATED_OUTCOME(false, PROJECT);

    private boolean nextStateSelection;

    private PrismScope scope;

    private PrismStateTransitionEvaluation(boolean nextStateSelection, PrismScope scope) {
        this.nextStateSelection = nextStateSelection;
        this.scope = scope;
    }

    public final boolean isNextStateSelection() {
        return nextStateSelection;
    }

    public final PrismScope getScope() {
        return scope;
    }

}
