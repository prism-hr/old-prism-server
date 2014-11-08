package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismDurationEvaluation.APPLICATION_INTERVIEW_DURATION_OUTCOME;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

public enum PrismDuration {

    APPLICATION_COMPLETE_REMINDER_DURATION(7, null, APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_REMINDER_DURATION(7, null, APPLICATION), //
    APPLICATION_PROVIDE_REVIEW_DURATION(7, null, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION(3, null, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_REMINDER_DURATION(1, null, APPLICATION), //
    APPLICATION_INTERVIEW_DURATION(null, APPLICATION_INTERVIEW_DURATION_OUTCOME, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION(7, null, APPLICATION), //
    APPLICATION_CONFIRM_SUPERVISION_DURATION(3, null, APPLICATION), //
    APPLICATION_ESCALATE_DURATION(28, null, APPLICATION), //
    APPLICATION_PURGE_DURATION(168, null, APPLICATION), //
    PROJECT_CORRECT_REMINDER_DURATION(3, null, PROJECT), //
    PROJECT_APPROVE_DURATION(28, null, PROJECT), //
    PROJECT_ESCALATE_DURATION(28, null, PROJECT), //
    PROGRAM_CORRECT_REMINDER_DURATION(3, null, PROGRAM), //
    PROGRAM_APPROVE_DURATION(28, null, PROGRAM), //
    PROGRAM_ESCALATE_DURATION(28, null, PROGRAM), //
    INSTITUTION_CORRECT_REMINDER_DURATION(3, null, INSTITUTION), //
    INSTITUTION_ESCALATE_DURATION(28, null, INSTITUTION), //
    SYSTEM_APPLICATION_TASK_REMINDER_DURATION(3, null, SYSTEM), //
    SYSTEM_PROJECT_TASK_REMINDER_DURATION(3, null, SYSTEM), //
    SYSTEM_PROGRAM_TASK_REMINDER_DURATION(3, null, SYSTEM), //
    SYSTEM_INSTITUTION_TASK_REMINDER_DURATION(3, null, SYSTEM);

    private Integer defaultDuration;

    private PrismDurationEvaluation durationEvaluation;

    private PrismScope scope;

    private PrismDuration(Integer defaultDuration, PrismDurationEvaluation durationEvaluation, PrismScope scope) {
        this.defaultDuration = defaultDuration;
        this.durationEvaluation = durationEvaluation;
        this.scope = scope;
    }

    public final Integer getDefaultDuration() {
        return defaultDuration;
    }

    public final boolean isDuration() {
        return defaultDuration != null;
    }

    public final PrismDurationEvaluation getDurationEvaluation() {
        return durationEvaluation;
    }

    public final PrismScope getScope() {
        return scope;
    }

}
