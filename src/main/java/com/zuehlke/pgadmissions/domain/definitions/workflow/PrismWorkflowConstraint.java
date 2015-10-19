package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismWorkflowConstraint {

    APPLICATION_QUALIFICATION(1, 10), //
    APPLICATION_EMPLOYMENT_POSITION(0, 10), //
    APPLICATION_REFEREE_ASSIGNMENT(2, 3), //
    APPLICATION_DOCUMENT_CV(1, 1), //
    APPLICATION_DOCUMENT_COVERING_LETTER(0, 1), //
    APPLICATION_REVIEWER_ASSIGNMENT(1, 999), //
    APPLICATION_INTERVIEWER_ASSIGNMENT(1, 999), //
    APPLICATION_HIRING_MANAGER_ASSIGNMENT(1, 999);

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
