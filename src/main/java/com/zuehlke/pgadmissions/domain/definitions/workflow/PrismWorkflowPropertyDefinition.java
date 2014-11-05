package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_DATA;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_WORKFLOW;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.PROJECT_DATA;

public enum PrismWorkflowPropertyDefinition {

    APPLICATION_THEME_PRIMARY(APPLICATION_DATA, 0, 10, true, 1, 1, APPLICATION), //
    APPLICATION_THEME_SECONDARY(APPLICATION_DATA, 0, 10, true, 0, 2, APPLICATION), //
    APPLICATION_SUGGESTED_SUPERVISOR(APPLICATION_DATA, 0, 10, true, 0, 5, APPLICATION), //
    APPLICATION_CREATOR_DEMOGRAPHIC(APPLICATION_DATA, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_LANGUAGE(APPLICATION_DATA, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_RESIDENCE(APPLICATION_DATA, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_QUALIFICATION(APPLICATION_DATA, 0, 10, true, 0, 10, APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION(APPLICATION_DATA, 0, 10, true, 0, 10, APPLICATION), //
    APPLICATION_FUNDING(APPLICATION_DATA, 0, 10, true, 0, 10, APPLICATION), //
    APPLICATION_REFEREE(APPLICATION_DATA, 3, 3, true, 3, 3, APPLICATION), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT(APPLICATION_DATA, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_DOCUMENT_CV(APPLICATION_DATA, 0, 1, true, 0, 1, APPLICATION), //
    APPLICATION_DOCUMENT_COVERING_LETTER(APPLICATION_DATA, 0, 1, true, 0, 0, APPLICATION), //
    APPLICATION_CRIMINAL_CONVICTION(APPLICATION_DATA, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_CONFIRM_ELIGIBILITY(APPLICATION_WORKFLOW, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_REVIEWER(APPLICATION_WORKFLOW, 1, 0, true, 1, 0, APPLICATION), //
    APPLICATION_INTERVIEWER(APPLICATION_WORKFLOW, 1, 0, true, 1, 0, APPLICATION), //
    APPLICATION_PRIMARY_SUPERVISOR(APPLICATION_WORKFLOW, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_SECONDARY_SUPERVISOR(APPLICATION_WORKFLOW, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_POSITION_DETAIL(APPLICATION_WORKFLOW, 1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_OFFER_DETAIL(APPLICATION_WORKFLOW, 1, 1, true, 1, 1, APPLICATION), //
    PROJECT_PRIMARY_SUPERVISOR(PROJECT_DATA, 1, 1, true, 1, 1, PROJECT), //
    PROJECT_SECONDARY_SUPERVISOR(PROJECT_DATA, 0, 1, true, 0, 1, PROJECT);

    private PrismWorkflowPropertyCategory workflowPropertyCategory;

    private Integer globalMinimum;

    private Integer globalMaximum;

    private boolean defaultEnabled;

    private Integer defaultMinimum;

    private Integer defaultMaximum;

    private PrismScope scope;

    private PrismWorkflowPropertyDefinition(PrismWorkflowPropertyCategory workflowPropertyCategory, Integer globalMinimum, Integer globalMaximum,
            boolean defaultEnabled, Integer defaultMinimum, Integer defaultMaximum, PrismScope scope) {
        this.workflowPropertyCategory = workflowPropertyCategory;
        this.globalMinimum = globalMinimum;
        this.globalMaximum = globalMaximum;
        this.defaultEnabled = defaultEnabled;
        this.defaultMinimum = defaultMinimum;
        this.defaultMaximum = defaultMaximum;
        this.scope = scope;
    }

    public final PrismWorkflowPropertyCategory getWorkflowPropertyCategory() {
        return workflowPropertyCategory;
    }

    public final boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    public final Integer getGlobalMinimum() {
        return globalMinimum;
    }

    public final Integer getGlobalMaximum() {
        return globalMaximum;
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

}
