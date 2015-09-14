package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_DATA;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_WORKFLOW;

public enum PrismWorkflowPropertyDefinition {

    APPLICATION_DEMOGRAPHIC(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION), //
    APPLICATION_QUALIFICATION(APPLICATION_DATA, 0, 10, null, null, 0, 10, APPLICATION), //
    APPLICATION_QUALIFICATION_PROOF_OF_AWARD(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION(APPLICATION_DATA, 0, 10, null, null, 0, 10, APPLICATION), //
    APPLICATION_ASSIGN_REFEREE(APPLICATION_DATA, 1, 999, null, null, 3, 3, APPLICATION), //
    APPLICATION_DOCUMENT_CV(APPLICATION_DATA, null, null, true, false, null, null, APPLICATION), //
    APPLICATION_DOCUMENT_COVERING_LETTER(APPLICATION_DATA, null, null, false, false, null, null, APPLICATION), //
    APPLICATION_CRIMINAL_CONVICTION(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION), //
    APPLICATION_ASSIGN_REVIEWER(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 999, APPLICATION), //
    APPLICATION_ASSIGN_INTERVIEWER(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 999, APPLICATION), //
    APPLICATION_ASSIGN_HIRING_MANAGER(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 1, APPLICATION);

    private PrismWorkflowPropertyCategory category;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private Boolean defaultEnabled;

    private Boolean defaultRequired;

    private Integer defaultMinimum;

    private Integer defaultMaximum;

    private PrismScope scope;

    private PrismWorkflowPropertyDefinition(PrismWorkflowPropertyCategory category, Integer minimumPermitted, Integer maximumPermitted, Boolean defaultEnabled,
            Boolean defaultRequired, Integer defaultMinimum, Integer defaultMaximum, PrismScope scope) {
        this.category = category;
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
        this.defaultEnabled = defaultEnabled;
        this.defaultRequired = defaultRequired;
        this.defaultMinimum = defaultMinimum;
        this.defaultMaximum = defaultMaximum;
        this.scope = scope;
    }

    public final PrismWorkflowPropertyCategory getCategory() {
        return category;
    }

    public final Integer getMinimumPermitted() {
        return minimumPermitted;
    }

    public final Integer getMaximumPermitted() {
        return maximumPermitted;
    }

    public final Boolean getDefaultEnabled() {
        return defaultEnabled;
    }

    public final Boolean getDefaultRequired() {
        return defaultRequired;
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

    public final boolean isDefineRange() {
        return !(minimumPermitted == null);
    }

    public final boolean isCanBeDisabled() {
        return isDefineRange() && minimumPermitted.equals(0) || !(defaultEnabled == null);
    }

    public final boolean isCanBeOptional() {
        return isDefineRange() && minimumPermitted.equals(0) || !(defaultRequired == null);
    }

}
