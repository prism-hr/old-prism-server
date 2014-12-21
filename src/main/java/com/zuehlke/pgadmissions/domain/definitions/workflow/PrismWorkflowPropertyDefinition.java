package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_DATA;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_WORKFLOW;

public enum PrismWorkflowPropertyDefinition {

    APPLICATION_STUDY_DETAIL(APPLICATION_DATA, null, null, false, null, null, null, APPLICATION, APPLICATION_STUDY_DETAIL_LABEL, APPLICATION_STUDY_DETAIL_LABEL), //
    APPLICATION_THEME_PRIMARY(APPLICATION_DATA, 0, 10, null, null, 0, 0, APPLICATION, APPLICATION_THEME_PRIMARY_LABEL, APPLICATION_THEME_PRIMARY_TOOLTIP), //
    APPLICATION_THEME_SECONDARY(APPLICATION_DATA, 0, 10, null, null, 0, 0, APPLICATION, APPLICATION_THEME_SECONDARY_LABEL, APPLICATION_THEME_SECONDARY_TOOLTIP), //
    APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR(APPLICATION_DATA, 0, 999, null, null, 0, 5, APPLICATION, APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR_LABEL,
            APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR_TOOLTIP), //
    APPLICATION_DEMOGRAPHIC(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, APPLICATION_CREATOR_DEMOGRAPHIC_LABEL,
            APPLICATION_CREATOR_DEMOGRAPHIC_TOOLTIP), //
    APPLICATION_LANGUAGE(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, APPLICATION_LANGUAGE_LABEL, APPLICATION_LANGUAGE_TOOLTIP), //
    APPLICATION_LANGUAGE_PROOF_OF_AWARD(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, APPLICATION_LANGUAGE_PROOF_OF_AWARD_LABEL,
            APPLICATION_LANGUAGE_PROOF_OF_AWARD_TOOLTIP), //
    APPLICATION_RESIDENCE(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, APPLICATION_RESIDENCE_LABEL, APPLICATION_RESIDENCE_TOOLTIP), //
    APPLICATION_QUALIFICATION(APPLICATION_DATA, 0, 10, null, null, 0, 10, APPLICATION, APPLICATION_QUALIFICATION_LABEL, APPLICATION_QUALIFICATION_TOOLTIP), //
    APPLICATION_QUALIFICATION_PROOF_OF_AWARD(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, APPLICATION_QUALIFICATION_PROOF_OF_AWARD_LABEL,
            APPLICATION_QUALIFICATION_PROOF_OF_AWARD_TOOLTIP), //
    APPLICATION_EMPLOYMENT_POSITION(APPLICATION_DATA, 0, 10, null, null, 0, 10, APPLICATION, APPLICATION_EMPLOYMENT_POSITION_LABEL,
            APPLICATION_EMPLOYMENT_POSITION_TOOLTIP), //
    APPLICATION_FUNDING(APPLICATION_DATA, 0, 10, null, null, 0, 10, APPLICATION, APPLICATION_FUNDING_LABEL, APPLICATION_FUNDING_TOOLTIP), //
    APPLICATION_FUNDING_PROOF_OF_AWARD(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, APPLICATION_FUNDING_PROOF_OF_AWARD_LABEL,
            APPLICATION_FUNDING_PROOF_OF_AWARD_TOOLTIP), //
    APPLICATION_PRIZE(APPLICATION_DATA, 0, 10, null, null, 0, 0, APPLICATION, APPLICATION_PRIZE_LABEL, APPLICATION_PRIZE_TOOLTIP), //
    APPLICATION_ASSIGN_REFEREE(APPLICATION_DATA, 1, 999, null, null, 3, 3, APPLICATION, APPLICATION_ASSIGN_REFEREE_LABEL, APPLICATION_ASSIGN_REFEREE_TOOLTIP), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL,
            APPLICATION_DOCUMENT_PERSONAL_STATEMENT_TOOLTIP), //
    APPLICATION_DOCUMENT_RESEARCH_STATEMENT(APPLICATION_DATA, null, null, false, false, null, null, APPLICATION, APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL,
            APPLICATION_DOCUMENT_RESEARCH_STATEMENT_TOOLTIP), //
    APPLICATION_DOCUMENT_CV(APPLICATION_DATA, null, null, true, false, null, null, APPLICATION, APPLICATION_DOCUMENT_CV_LABEL, APPLICATION_DOCUMENT_CV_TOOLTIP), //
    APPLICATION_DOCUMENT_COVERING_LETTER(APPLICATION_DATA, null, null, false, false, null, null, APPLICATION, APPLICATION_DOCUMENT_COVERING_LETTER_LABEL,
            APPLICATION_DOCUMENT_COVERING_LETTER_TOOLTIP), //
    APPLICATION_CRIMINAL_CONVICTION(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, APPLICATION_CRIMINAL_CONVICTION_LABEL,
            APPLICATION_CRIMINAL_CONVICTION_TOOLTIP), //
    APPLICATION_ASSIGN_REVIEWER(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 999, APPLICATION, APPLICATION_ASSIGN_REVIEWER_LABEL,
            APPLICATION_ASSIGN_REVIEWER_TOOLTIP), //
    APPLICATION_ASSIGN_INTERVIEWER(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 999, APPLICATION, APPLICATION_ASSIGN_INTERVIEWER_LABEL,
            APPLICATION_ASSIGN_INTERVIEWER_TOOLTIP), //
    APPLICATION_ASSIGN_PRIMARY_SUPERVISOR(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 1, APPLICATION, APPLICATION_ASSIGN_PRIMARY_SUPERVISOR_LABEL,
            APPLICATION_ASSIGN_PRIMARY_SUPERVISOR_TOOLTIP), //
    APPLICATION_ASSIGN_SECONDARY_SUPERVISOR(APPLICATION_WORKFLOW, 0, 999, null, null, 1, 1, APPLICATION, APPLICATION_ASSIGN_SECONDARY_SUPERVISOR_LABEL,
            APPLICATION_ASSIGN_SECONDARY_SUPERVISOR_TOOLTIP), //
    APPLICATION_POSITION_DETAIL(APPLICATION_WORKFLOW, null, null, true, null, null, null, APPLICATION, APPLICATION_POSITION_DETAIL_LABEL,
            APPLICATION_POSITION_DETAIL_TOOLTIP), //
    APPLICATION_OFFER_DETAIL(APPLICATION_WORKFLOW, null, null, true, null, null, null, APPLICATION, APPLICATION_OFFER_DETAIL_LABEL,
            APPLICATION_OFFER_DETAIL_TOOLTIP);

    private PrismWorkflowPropertyCategory category;

    private Integer minimumPermitted;

    private Integer maximumPermitted;

    private Boolean defaultEnabled;

    private Boolean defaultRequired;

    private Integer defaultMinimum;

    private Integer defaultMaximum;

    private PrismScope scope;

    private PrismDisplayPropertyDefinition label;

    private PrismDisplayPropertyDefinition tooltip;

    private PrismWorkflowPropertyDefinition(PrismWorkflowPropertyCategory category, Integer minimumPermitted, Integer maximumPermitted, Boolean defaultEnabled,
            Boolean defaultRequired, Integer defaultMinimum, Integer defaultMaximum, PrismScope scope, PrismDisplayPropertyDefinition label,
            PrismDisplayPropertyDefinition tooltip) {
        this.category = category;
        this.minimumPermitted = minimumPermitted;
        this.maximumPermitted = maximumPermitted;
        this.defaultEnabled = defaultEnabled;
        this.defaultRequired = defaultRequired;
        this.defaultMinimum = defaultMinimum;
        this.defaultMaximum = defaultMaximum;
        this.scope = scope;
        this.label = label;
        this.tooltip = tooltip;
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

    public final PrismDisplayPropertyDefinition getLabel() {
        return label;
    }

    public final PrismDisplayPropertyDefinition getTooltip() {
        return tooltip;
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
