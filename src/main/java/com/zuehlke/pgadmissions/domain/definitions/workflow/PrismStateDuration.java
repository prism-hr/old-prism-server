package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

public enum PrismStateDuration {

    APPLICATION_PROVIDE_REVIEW_DURATION(7, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION(3, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION(7, APPLICATION), //
    APPLICATION_CONFIRM_SUPERVISION_DURATION(3, APPLICATION), //
    APPLICATION_ESCALATE_DURATION(28, APPLICATION), //
    APPLICATION_PURGE_DURATION(168, APPLICATION), //
    PROJECT_APPROVE_DURATION(28, PROJECT), //
    PROJECT_ESCALATE_DURATION(28, PROJECT), //
    PROGRAM_ESCALATE_DURATION(28, PROGRAM), //
    INSTITUTION_ESCALATE_DURATION(28, INSTITUTION), //
    SYSTEM_APPLICATION_TASK_REMINDER_DURATION(3, SYSTEM), //
    SYSTEM_PROJECT_TASK_REMINDER_DURATION(3, SYSTEM), //
    SYSTEM_PROGRAM_TASK_REMINDER_DURATION(3, SYSTEM), //
    SYSTEM_INSTITUTION_TASK_REMINDER_DURATION(3, SYSTEM);

    private Integer defaultDuration;

    private PrismScope scope;

    private PrismStateDuration(Integer defaultDuration, PrismScope scope) {
        this.defaultDuration = defaultDuration;
        this.scope = scope;
    }

    public final Integer getDefaultDuration() {
        return defaultDuration;
    }

    public final PrismScope getScope() {
        return scope;
    }

}
