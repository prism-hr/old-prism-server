package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_DATA;

public enum PrismWorkflowPropertyDefinition {

    APPLICATION_THEME_PRIMARY(APPLICATION_DATA, true, 0, 10, 1, 1, null, APPLICATION), //
    APPLICATION_THEME_SECONDARY(APPLICATION_DATA, true, 0, 10, 0, 2, null, APPLICATION), //
    APPLICATION_SUGGESTED_SUPERVISOR(APPLICATION_DATA, true, 0, 10, 0, 5, null, APPLICATION), //
    APPLICATION_CREATOR_DEMOGRAPHIC(APPLICATION_DATA, true, 0, 1, 1, 1, null, APPLICATION);

    private PrismWorkflowPropertyCategory workflowPropertyCategory;

    private boolean defaultEnabled;

    private Integer globalMinimum;

    private Integer globalMaximum;

    private Integer defaultMinimum;

    private Integer defaultMaximum;

    private PrismState defaultState;

    private PrismScope scope;

    private PrismWorkflowPropertyDefinition(PrismWorkflowPropertyCategory workflowPropertyCategory, boolean defaultEnabled, Integer globalMinimum,
            Integer globalMaximum, Integer defaultMinimum, Integer defaultMaximum, PrismState defaultState, PrismScope scope) {
        this.workflowPropertyCategory = workflowPropertyCategory;
        this.defaultEnabled = defaultEnabled;
        this.globalMinimum = globalMinimum;
        this.globalMaximum = globalMaximum;
        this.defaultMinimum = defaultMinimum;
        this.defaultMaximum = defaultMaximum;
        this.defaultState = defaultState;
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

    public final PrismState getDefaultState() {
        return defaultState;
    }

    public final PrismScope getScope() {
        return scope;
    }

}
