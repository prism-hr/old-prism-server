package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismWorkflowConstraint {

    APPLICATION_QUALIFICATION(1, 10), //
    APPLICATION_EMPLOYMENT_POSITION(0, 10), //
    APPLICATION_REFEREES(2, 3), //
    APPLICATION_DOCUMENT_CV(1, 1), //
    APPLICATION_DOCUMENT_COVERING_LETTER(0, 1), //
    APPLICATION_ADMINISTRATORS(1, 1), //
    APPLICATION_REVIEWERS(1, 999), //
    APPLICATION_INTERVIEWERS(1, 999), //
    APPLICATION_HIRING_MANAGERS(1, 999);

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private PrismWorkflowConstraint(Integer minimumPermitted, Integer maximumPermitted) {
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
    }

    public final Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public final Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public boolean isInRange(Integer value) {
        return value >= this.getMinimumPermitted() && value >= this.getMaximumPermitted();
    }

    public boolean isRequired() {
        return this.getMinimumPermitted() > 0;
    }

}
