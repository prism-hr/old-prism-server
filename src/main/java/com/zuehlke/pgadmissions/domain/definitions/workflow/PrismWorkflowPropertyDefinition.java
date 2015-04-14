package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_INTERVIEWER_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_INTERVIEWER_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_PRIMARY_SUPERVISOR_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_PRIMARY_SUPERVISOR_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_REFEREE_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_REFEREE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_REVIEWER_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_REVIEWER_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_SECONDARY_SUPERVISOR_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_SECONDARY_SUPERVISOR_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_CRIMINAL_CONVICTION_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_CRIMINAL_CONVICTION_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DEMOGRAPHIC_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DEMOGRAPHIC_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DOCUMENT_COVERING_LETTER_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DOCUMENT_COVERING_LETTER_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DOCUMENT_CV_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DOCUMENT_CV_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DOCUMENT_PERSONAL_STATEMENT_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DOCUMENT_RESEARCH_STATEMENT_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_EMPLOYMENT_POSITION_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_EMPLOYMENT_POSITION_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_FUNDING_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_FUNDING_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_FUNDING_PROOF_OF_AWARD_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_FUNDING_PROOF_OF_AWARD_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_LANGUAGE_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_LANGUAGE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_LANGUAGE_PROOF_OF_AWARD_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_LANGUAGE_PROOF_OF_AWARD_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_OFFER_DETAIL_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_OFFER_DETAIL_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_POSITION_DETAIL_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_POSITION_DETAIL_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_PRIZE_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_PRIZE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_QUALIFICATION_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_QUALIFICATION_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_QUALIFICATION_PROOF_OF_AWARD_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_QUALIFICATION_PROOF_OF_AWARD_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_RESIDENCE_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_RESIDENCE_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_STUDY_DETAIL_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_THEME_PRIMARY_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_THEME_PRIMARY_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_THEME_SECONDARY_HINT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_THEME_SECONDARY_LABEL;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_DATA;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory.APPLICATION_WORKFLOW;

import com.zuehlke.pgadmissions.domain.definitions.PrismConfigurationLocalizable;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;

public enum PrismWorkflowPropertyDefinition implements PrismConfigurationLocalizable {

    APPLICATION_STUDY_DETAIL(APPLICATION_DATA, null, null, false, null, null, null, APPLICATION, SYSTEM_APPLICATION_STUDY_DETAIL_LABEL, SYSTEM_APPLICATION_STUDY_DETAIL_LABEL), //
    APPLICATION_THEME_PRIMARY(APPLICATION_DATA, 0, 10, null, null, 0, 0, APPLICATION, SYSTEM_APPLICATION_THEME_PRIMARY_LABEL, SYSTEM_APPLICATION_THEME_PRIMARY_HINT), //
    APPLICATION_THEME_SECONDARY(APPLICATION_DATA, 0, 10, null, null, 0, 0, APPLICATION, SYSTEM_APPLICATION_THEME_SECONDARY_LABEL, SYSTEM_APPLICATION_THEME_SECONDARY_HINT), //
    APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR(APPLICATION_DATA, 0, 999, null, null, 0, 5, APPLICATION, SYSTEM_APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR_LABEL,
            SYSTEM_APPLICATION_ASSIGN_SUGGESTED_SUPERVISOR_HINT), //
    APPLICATION_DEMOGRAPHIC(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, SYSTEM_APPLICATION_DEMOGRAPHIC_LABEL,
            SYSTEM_APPLICATION_DEMOGRAPHIC_HINT), //
    APPLICATION_LANGUAGE(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, SYSTEM_APPLICATION_LANGUAGE_LABEL, SYSTEM_APPLICATION_LANGUAGE_HINT), //
    APPLICATION_LANGUAGE_PROOF_OF_AWARD(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, SYSTEM_APPLICATION_LANGUAGE_PROOF_OF_AWARD_LABEL,
            SYSTEM_APPLICATION_LANGUAGE_PROOF_OF_AWARD_HINT), //
    APPLICATION_RESIDENCE(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, SYSTEM_APPLICATION_RESIDENCE_LABEL, SYSTEM_APPLICATION_RESIDENCE_HINT), //
    APPLICATION_QUALIFICATION(APPLICATION_DATA, 0, 10, null, null, 0, 10, APPLICATION, SYSTEM_APPLICATION_QUALIFICATION_LABEL, SYSTEM_APPLICATION_QUALIFICATION_HINT), //
    APPLICATION_QUALIFICATION_PROOF_OF_AWARD(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, SYSTEM_APPLICATION_QUALIFICATION_PROOF_OF_AWARD_LABEL,
            SYSTEM_APPLICATION_QUALIFICATION_PROOF_OF_AWARD_HINT), //
    APPLICATION_EMPLOYMENT_POSITION(APPLICATION_DATA, 0, 10, null, null, 0, 10, APPLICATION, SYSTEM_APPLICATION_EMPLOYMENT_POSITION_LABEL,
            SYSTEM_APPLICATION_EMPLOYMENT_POSITION_HINT), //
    APPLICATION_FUNDING(APPLICATION_DATA, 0, 10, null, null, 0, 10, APPLICATION, SYSTEM_APPLICATION_FUNDING_LABEL, SYSTEM_APPLICATION_FUNDING_HINT), //
    APPLICATION_FUNDING_PROOF_OF_AWARD(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, SYSTEM_APPLICATION_FUNDING_PROOF_OF_AWARD_LABEL,
            SYSTEM_APPLICATION_FUNDING_PROOF_OF_AWARD_HINT), //
    APPLICATION_PRIZE(APPLICATION_DATA, 0, 10, null, null, 0, 0, APPLICATION, SYSTEM_APPLICATION_PRIZE_LABEL, SYSTEM_APPLICATION_PRIZE_HINT), //
    APPLICATION_ASSIGN_REFEREE(APPLICATION_DATA, 1, 999, null, null, 3, 3, APPLICATION, SYSTEM_APPLICATION_ASSIGN_REFEREE_LABEL, SYSTEM_APPLICATION_ASSIGN_REFEREE_HINT), //
    APPLICATION_DOCUMENT_PERSONAL_STATEMENT(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, SYSTEM_APPLICATION_DOCUMENT_PERSONAL_STATEMENT_LABEL,
            SYSTEM_APPLICATION_DOCUMENT_PERSONAL_STATEMENT_HINT), //
    APPLICATION_DOCUMENT_RESEARCH_STATEMENT(APPLICATION_DATA, null, null, false, false, null, null, APPLICATION, SYSTEM_APPLICATION_DOCUMENT_RESEARCH_STATEMENT_LABEL,
            SYSTEM_APPLICATION_DOCUMENT_RESEARCH_STATEMENT_HINT), //
    APPLICATION_DOCUMENT_CV(APPLICATION_DATA, null, null, true, false, null, null, APPLICATION, SYSTEM_APPLICATION_DOCUMENT_CV_LABEL, SYSTEM_APPLICATION_DOCUMENT_CV_HINT), //
    APPLICATION_DOCUMENT_COVERING_LETTER(APPLICATION_DATA, null, null, false, false, null, null, APPLICATION, SYSTEM_APPLICATION_DOCUMENT_COVERING_LETTER_LABEL,
            SYSTEM_APPLICATION_DOCUMENT_COVERING_LETTER_HINT), //
    APPLICATION_CRIMINAL_CONVICTION(APPLICATION_DATA, null, null, true, true, null, null, APPLICATION, SYSTEM_APPLICATION_CRIMINAL_CONVICTION_LABEL,
            SYSTEM_APPLICATION_CRIMINAL_CONVICTION_HINT), //
    APPLICATION_ASSIGN_REVIEWER(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 999, APPLICATION, SYSTEM_APPLICATION_ASSIGN_REVIEWER_LABEL,
            SYSTEM_APPLICATION_ASSIGN_REVIEWER_HINT), //
    APPLICATION_ASSIGN_INTERVIEWER(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 999, APPLICATION, SYSTEM_APPLICATION_ASSIGN_INTERVIEWER_LABEL,
            SYSTEM_APPLICATION_ASSIGN_INTERVIEWER_HINT), //
    APPLICATION_ASSIGN_PRIMARY_SUPERVISOR(APPLICATION_WORKFLOW, 1, 999, null, null, 1, 1, APPLICATION, SYSTEM_APPLICATION_ASSIGN_PRIMARY_SUPERVISOR_LABEL,
            SYSTEM_APPLICATION_ASSIGN_PRIMARY_SUPERVISOR_HINT), //
    APPLICATION_ASSIGN_SECONDARY_SUPERVISOR(APPLICATION_WORKFLOW, 0, 999, null, null, 1, 1, APPLICATION, SYSTEM_APPLICATION_ASSIGN_SECONDARY_SUPERVISOR_LABEL,
            SYSTEM_APPLICATION_ASSIGN_SECONDARY_SUPERVISOR_HINT), //
    APPLICATION_POSITION_DETAIL(APPLICATION_WORKFLOW, null, null, true, null, null, null, APPLICATION, SYSTEM_APPLICATION_POSITION_DETAIL_LABEL,
            SYSTEM_APPLICATION_POSITION_DETAIL_HINT), //
    APPLICATION_OFFER_DETAIL(APPLICATION_WORKFLOW, null, null, true, null, null, null, APPLICATION, SYSTEM_APPLICATION_OFFER_DETAIL_LABEL,
            SYSTEM_APPLICATION_OFFER_DETAIL_HINT);

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
