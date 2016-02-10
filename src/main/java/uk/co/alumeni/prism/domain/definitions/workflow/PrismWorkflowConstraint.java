package uk.co.alumeni.prism.domain.definitions.workflow;

public enum PrismWorkflowConstraint {

    APPLICATION_THEME(1, 3),
    APPLICATION_LOCATION(1, 1),
    APPLICATION_QUALIFICATION(1, 10),
    APPLICATION_AWARD(0, 10),
    APPLICATION_EMPLOYMENT_POSITION(0, 10),
    APPLICATION_REFEREE_ASSIGNMENT(1, 1),
    APPLICATION_DOCUMENT_CV(0, 1),
    APPLICATION_DOCUMENT_COVERING_LETTER(0, 1),
    APPLICATION_REVIEWER_ASSIGNMENT(1, 999),
    APPLICATION_INTERVIEWER_ASSIGNMENT(1, 999),
    APPLICATION_HIRING_MANAGER_ASSIGNMENT(1, 999),
    USER_ACCOUNT_QUALIFICATION(0, 10),
    USER_ACCOUNT_AWARD(0, 10), 
    USER_ACCOUNT_EMPLOYMENT_POSITION(0, 10),
    USER_ACCOUNT_REFEREE_ASSIGNMENT(0, 10),
    USER_ACCOUNT_DOCUMENT_CV(0, 1),
    USER_ACCOUNT_DOCUMENT_COVERING_LETTER(1, 1);

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    PrismWorkflowConstraint(Integer minimumPermitted, Integer maximumPermitted) {
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
    }

    public Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public Integer getMaximumPermitted() {
        return maximumPermitted;
    }


    public boolean isRequired() {
        return this.getMinimumPermitted() > 0;
    }

}
