package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_DATA;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_ROLE_ASSIGNMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.PROJECT_ROLE_ASSIGNMENT;

public enum PrismWorkflowProperty {

    APPLICATION_THEME_PRIMARY(APPLICATION_DATA, null, 10, true, 1, 1, APPLICATION), //
    APPLICATION_THEME_SECONDARY(APPLICATION_DATA, null, 10, true, null, 2, APPLICATION), //
    APPLICATION_CREATOR_DEMOGRAPHIC(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    APPLICATION_LANGUAGE(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    APPLICATION_RESIDENCE(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    APPLICATION_QUALIFICATION(APPLICATION_DATA, null, 10, true, null, 10, APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION(APPLICATION_DATA, null, 10, true, null, 10, APPLICATION), //
    APPLICATION_FUNDING(APPLICATION_DATA, null, 10, true, null, 10, APPLICATION), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    APPLICATION_DOCUMENT_CV(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    APPLICATION_DOCUMENT_COVERING_LETTER(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    APPLICATION_CRIMINAL_CONVICTION(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR(APPLICATION_ROLE_ASSIGNMENT, null, 999, true, null, 5, APPLICATION), //
    APPLICATION_ASSIGN_REFEREE(APPLICATION_ROLE_ASSIGNMENT, null, 999, true, 3, 3, APPLICATION), //
    APPLICATION_ASSIGN_REVIEWER(APPLICATION_ROLE_ASSIGNMENT, 1, 999, true, 1, 0, APPLICATION), //
    APPLICATION_ASSIGN_INTERVIEWER(APPLICATION_ROLE_ASSIGNMENT, 1, 999, true, 1, 0, APPLICATION), //
    APPLICATION_ASSIGN_PRIMARY_SUPERVISOR(APPLICATION_ROLE_ASSIGNMENT, 1, 999, true, 1, 1, APPLICATION), //
    APPLICATION_ASSIGN_SECONDARY_SUPERVISOR(APPLICATION_ROLE_ASSIGNMENT, null, 999, true, 1, 1, APPLICATION), //
    APPLICATION_POSITION_DETAIL(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    APPLICATION_OFFER_DETAIL(APPLICATION_DATA, null, null, true, null, null, APPLICATION), //
    PROJECT_PRIMARY_SUPERVISOR(PROJECT_ROLE_ASSIGNMENT, 1, 999, true, 1, 1, PROJECT), //
    PROJECT_SECONDARY_SUPERVISOR(PROJECT_ROLE_ASSIGNMENT, null, 999, true, null, 1, PROJECT);

    private PrismWorkflowPropertyCategory workflowPropertyCategory;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private boolean defaultEnabled;

    private Integer defaultMinimum;

    private Integer defaultMaximum;

    private PrismScope scope;

    private PrismWorkflowProperty(PrismWorkflowPropertyCategory workflowPropertyCategory, Integer minimumPermitted, Integer maximumPermitted,
            boolean defaultEnabled, Integer defaultMinimum, Integer defaultMaximum, PrismScope scope) {
        this.workflowPropertyCategory = workflowPropertyCategory;
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
        this.defaultEnabled = defaultEnabled;
        this.defaultMinimum = defaultMinimum;
        this.defaultMaximum = defaultMaximum;
        this.scope = scope;
    }

    public final PrismWorkflowPropertyCategory getWorkflowPropertyCategory() {
        return workflowPropertyCategory;
    }

    public final Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public final Integer getMaximumPermitted() {
        return maximumPermitted;
    }
    
    public final boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    public final Integer getDefaultMinimum() {
        return defaultMinimum;
    }

    public final Integer getDefaultMaximum() {
        return defaultMaximum;
    }

    public final PrismScope getScope() {
        return scope;
    }

    public final boolean isOptional() {
        return minimumPermitted == null;
    }
    
    public final boolean isRangeSpecification() {
        return !(minimumPermitted == null && maximumPermitted == null);
    }

}
