package uk.co.alumeni.prism.domain.definitions.workflow;

public enum PrismStateDurationDefinition {

    APPLICATION_CONFIRM_ELIGIBILITY_DURATION(3, false, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_DURATION(7, false, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_REVIEW_DURATION(7, false, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_AVAILABILITY_DURATION(3, false, PrismScope.APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_DURATION(7, false, PrismScope.APPLICATION), //
    APPLICATION_CONFIRM_APPOINTMENT_DURATION(3, false, PrismScope.APPLICATION), //
    APPLICATION_RESERVE_DURATION(28, false, PrismScope.APPLICATION), //
    APPLICATION_RESERVE_ESCALATE_DURATION(14, false, PrismScope.APPLICATION), //
    APPLICATION_ESCALATE_DURATION(84, true, PrismScope.APPLICATION), //
    PROJECT_ESCALATE_DURATION(28, true, PrismScope.PROJECT), //
    PROGRAM_ESCALATE_DURATION(28, true, PrismScope.PROGRAM), //
    DEPARTMENT_ESCALATE_DURATION(28, true, PrismScope.DEPARTMENT), //
    INSTITUTION_ESCALATE_DURATION(28, true, PrismScope.INSTITUTION);

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
