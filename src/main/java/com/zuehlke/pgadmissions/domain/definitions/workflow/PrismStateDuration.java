package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

public enum PrismStateDuration {

    APPLICATION_PROVIDE_REVIEW_DURATION(7, true, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION(3, null, APPLICATION), //
    APPLICATION_INTERVIEW_DURATION(null, true, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION(7, null, APPLICATION), //
    APPLICATION_CONFIRM_SUPERVISION_DURATION(3, null, APPLICATION), //
    APPLICATION_ESCALATE_DURATION(28, null, APPLICATION), //
    APPLICATION_PURGE_DURATION(168, null, APPLICATION), //
    PROJECT_APPROVE_DURATION(28, null, PROJECT), //
    PROJECT_ESCALATE_DURATION(28, null, PROJECT), //
    PROGRAM_APPROVE_DURATION(28, null, PROGRAM), //
    PROGRAM_ESCALATE_DURATION(28, null, PROGRAM), //
    INSTITUTION_ESCALATE_DURATION(28, null, INSTITUTION);

    private Integer defaultDuration;

    private Boolean evaluation;

    private PrismScope scope;

    private PrismStateDuration(Integer defaultDuration, Boolean evaluation, PrismScope scope) {
        this.defaultDuration = defaultDuration;
        this.evaluation = evaluation;
        this.scope = scope;
    }

    public final Integer getDefaultDuration() {
        return defaultDuration;
    }

    public final boolean isDuration() {
        return defaultDuration != null;
    }

    public final boolean isEvaluation() {
        return evaluation;
    }

    public final PrismScope getScope() {
        return scope;
    }

}
