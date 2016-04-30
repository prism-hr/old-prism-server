package uk.co.alumeni.prism.domain.definitions.workflow;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;

public enum PrismStateDurationDefinition {

    APPLICATION_MESSAGE_DURATION(28, false, APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_DURATION(7, false, APPLICATION), //
    APPLICATION_PROVIDE_REVIEW_DURATION(7, false, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION(3, false, APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION(7, false, APPLICATION), //
    APPLICATION_CONFIRM_APPOINTMENT_DURATION(3, false, APPLICATION), //
    APPLICATION_RESERVE_DURATION(28, false, APPLICATION), //
    APPLICATION_RESERVE_ESCALATE_DURATION(14, false, APPLICATION), //
    APPLICATION_ESCALATE_DURATION(84, true, APPLICATION), //
    PROJECT_ESCALATE_DURATION(28, true, PROJECT), //
    PROGRAM_ESCALATE_DURATION(28, true, PROGRAM), //
    DEPARTMENT_ESCALATE_DURATION(28, true, DEPARTMENT), //
    INSTITUTION_ESCALATE_DURATION(28, true, INSTITUTION);

    private Integer defaultDuration;

    private boolean escalation;

    private PrismScope scope;

    PrismStateDurationDefinition(Integer defaultDuration, boolean escalation, PrismScope scope) {
        this.defaultDuration = defaultDuration;
        this.escalation = escalation;
        this.scope = scope;
    }

    public Integer getDefaultDuration() {
        return defaultDuration;
    }

    public boolean isEscalation() {
        return escalation;
    }

    public PrismScope getScope() {
        return scope;
    }

}
