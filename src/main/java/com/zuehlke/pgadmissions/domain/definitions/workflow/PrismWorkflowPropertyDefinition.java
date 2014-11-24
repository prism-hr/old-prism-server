package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

public enum PrismWorkflowPropertyDefinition {

    APPLICATION_STUDY_DETAIL(null, null, false, null, null, APPLICATION), //
    APPLICATION_THEME_PRIMARY(null, 10, true, 1, 1, APPLICATION), //
    APPLICATION_THEME_SECONDARY(null, 10, true, null, 2, APPLICATION), //
    APPLICATION_CREATOR_DEMOGRAPHIC(null, null, true, null, null, APPLICATION), //
    APPLICATION_LANGUAGE(null, null, true, null, null, APPLICATION), //
    APPLICATION_LANGUAGE_PROOF_OF_COMPETENCE(1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_RESIDENCE(null, null, true, null, null, APPLICATION), //
    APPLICATION_QUALIFICATION(null, 10, true, null, 10, APPLICATION), //
    APPLICATION_QUALIFICATION_PROOF_OF_AWARD(1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION(null, 10, true, null, 10, APPLICATION), //
    APPLICATION_FUNDING(null, 10, true, null, 10, APPLICATION), //
    APPLICATION_FUNDING_PROOF_OF_AWARD(1, 1, true, 1, 1, APPLICATION), //
    APPLICATION_PRIZE(null, 10, true, null, 10, APPLICATION), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT(null, null, true, null, null, APPLICATION), //
    APPLICATION_DOCUMENT_RESEARCH_STATEMENT(null, null, false, null, null, APPLICATION), //
    APPLICATION_DOCUMENT_CV(null, null, true, null, null, APPLICATION), //
    APPLICATION_DOCUMENT_COVERING_LETTER(null, null, true, null, null, APPLICATION), //
    APPLICATION_CRIMINAL_CONVICTION(null, null, true, null, null, APPLICATION), //
    APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR(null, 999, true, null, 5, APPLICATION), //
    APPLICATION_ASSIGN_REFEREE(null, 999, true, 3, 3, APPLICATION), //
    APPLICATION_ASSIGN_REVIEWER(1, 999, true, 1, 999, APPLICATION), //
    APPLICATION_ASSIGN_INTERVIEWER(1, 999, true, 1, 999, APPLICATION), //
    APPLICATION_ASSIGN_PRIMARY_SUPERVISOR(1, 999, true, 1, 1, APPLICATION), //
    APPLICATION_ASSIGN_SECONDARY_SUPERVISOR(null, 999, true, 1, 1, APPLICATION), //
    APPLICATION_POSITION_DETAIL(null, null, true, null, null, APPLICATION), //
    APPLICATION_OFFER_DETAIL(null, null, true, null, null, APPLICATION), //
    PROJECT_PRIMARY_SUPERVISOR(1, 999, true, 1, 1, PROJECT), //
    PROJECT_SECONDARY_SUPERVISOR(null, 999, true, null, 1, PROJECT);

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private boolean defaultEnabled;

    private Integer defaultMinimum;

    private Integer defaultMaximum;

    private PrismScope scope;

    private PrismWorkflowPropertyDefinition(Integer minimumPermitted, Integer maximumPermitted, boolean defaultEnabled, Integer defaultMinimum,
            Integer defaultMaximum, PrismScope scope) {
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
        this.defaultEnabled = defaultEnabled;
        this.defaultMinimum = defaultMinimum;
        this.defaultMaximum = defaultMaximum;
        this.scope = scope;
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

    public final boolean isRangeSpecification() {
        return !(minimumPermitted == null && maximumPermitted == null);
    }

}
