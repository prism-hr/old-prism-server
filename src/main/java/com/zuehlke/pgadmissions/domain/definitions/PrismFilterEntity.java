package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByImportedRejectionReasonSelector;
import com.zuehlke.pgadmissions.workflow.selectors.summary.PrismResourceSummarySelector;

public enum PrismFilterEntity {

    FILTER_DOMICILE("application_personal_detail.imported_domicile_id"), //
    FILTER_NATIONALITY("application_personal_detail.imported_nationality_id"), //
    FILTER_AGE_RANGE("application_personal_detail.imported_age_range_id"), //
    FILTER_DISABILITY("application_personal_detail.imported_disability_id"), //
    FILTER_ETHNICITY("application_personal_detail.imported_ethnicity_id"), //
    FILTER_GENDER("application_personal_detail.imported_gender_id"), //
    FILTER_OPPORTUNITY_TYPE("application_program_detail.imported_opportunity_type_id"), //
    FILTER_REJECTION_REASON("application.id", ApplicationByImportedRejectionReasonSelector.class), //
    FILTER_STUDY_OPTION("application_program_detail.imported_study_option_id"), //
    FILTER_UNIVERSITY_INSTITUTION(""), //
    FILTER_UNIVERSITY_DEPARTMENT(""), //
    FILTER_UNIVERSITY_PROGRAM(""), //
    FILTER_UNIVERSITY_PROJECT(""), //
    FILTER_EMPLOYER_INSTITUTION(""), //
    FILTER_EMPLOYER_DEPARTMENT(""),;

    private String filterColumn;

    private Class<? extends PrismResourceSummarySelector> filterValueSelector;

    private PrismFilterEntity(String filterColumn) {
        this.filterColumn = filterColumn;
    }

    private PrismFilterEntity(String filterColumn, Class<? extends PrismResourceSummarySelector> filterValueSelector) {
        this.filterColumn = filterColumn;
        this.filterValueSelector = filterValueSelector;
    }

    public String getFilterColumn() {
        return filterColumn;
    }

    public Class<? extends PrismResourceSummarySelector> getFilterSelector() {
        return filterValueSelector;
    }

}
