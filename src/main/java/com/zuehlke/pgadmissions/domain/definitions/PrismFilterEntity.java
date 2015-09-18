package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByEmployingResourceScope;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByImportedRejectionReasonSelector;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByQualifyingResourceScopeSelector;
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
    FILTER_UNIVERSITY_INSTITUTION("application.id", INSTITUTION, ApplicationByQualifyingResourceScopeSelector.class), //
    FILTER_UNIVERSITY_DEPARTMENT("application.id", DEPARTMENT, ApplicationByQualifyingResourceScopeSelector.class), //
    FILTER_UNIVERSITY_PROGRAM("application.id", PROGRAM, ApplicationByQualifyingResourceScopeSelector.class), //
    FILTER_UNIVERSITY_PROJECT("application.id", PROJECT, ApplicationByQualifyingResourceScopeSelector.class), //
    FILTER_EMPLOYER_INSTITUTION("application.id", INSTITUTION, ApplicationByEmployingResourceScope.class), //
    FILTER_EMPLOYER_DEPARTMENT("application.id", DEPARTMENT, ApplicationByEmployingResourceScope.class);

    private String filterColumn;

    private PrismScope filterScope;

    private Class<? extends PrismResourceSummarySelector> filterValueSelector;

    private PrismFilterEntity(String filterColumn) {
        this.filterColumn = filterColumn;
    }

    private PrismFilterEntity(String filterColumn, Class<? extends PrismResourceSummarySelector> filterValueSelector) {
        this.filterColumn = filterColumn;
        this.filterValueSelector = filterValueSelector;
    }

    private PrismFilterEntity(String filterColumn, PrismScope filterScope, Class<? extends PrismResourceSummarySelector> filterValueSelector) {
        this.filterColumn = filterColumn;
        this.filterScope = filterScope;
        this.filterValueSelector = filterValueSelector;
    }

    public String getFilterColumn() {
        return filterColumn;
    }

    public PrismScope getFilterScope() {
        return filterScope;
    }

    public Class<? extends PrismResourceSummarySelector> getFilterSelector() {
        return filterValueSelector;
    }

}
