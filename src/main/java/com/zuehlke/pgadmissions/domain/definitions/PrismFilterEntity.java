package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByEmployingResourceScope;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByImportedRejectionReasonSelector;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByQualifyingResourceScopeSelector;
import com.zuehlke.pgadmissions.workflow.selectors.summary.PrismResourceSummarySelector;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.*;

public enum PrismFilterEntity {

    DOMICILE("application_personal_detail.imported_domicile_id"), //
    NATIONALITY("application_personal_detail.imported_nationality_id"), //
    AGE_RANGE("application_personal_detail.imported_age_range_id"), //
    DISABILITY("application_personal_detail.imported_disability_id"), //
    ETHNICITY("application_personal_detail.imported_ethnicity_id"), //
    GENDER("application_personal_detail.imported_gender_id"), //
    OPPORTUNITY_TYPE("application_program_detail.imported_opportunity_type_id"), //
    REJECTION_REASON("application.id", ApplicationByImportedRejectionReasonSelector.class), //
    STUDY_OPTION("application_program_detail.imported_study_option_id"), //
    UNIVERSITY_INSTITUTION("application.id", INSTITUTION, ApplicationByQualifyingResourceScopeSelector.class), //
    UNIVERSITY_DEPARTMENT("application.id", DEPARTMENT, ApplicationByQualifyingResourceScopeSelector.class), //
    UNIVERSITY_PROGRAM("application.id", PROGRAM, ApplicationByQualifyingResourceScopeSelector.class), //
    UNIVERSITY_PROJECT("application.id", PROJECT, ApplicationByQualifyingResourceScopeSelector.class), //
    EMPLOYER_INSTITUTION("application.id", INSTITUTION, ApplicationByEmployingResourceScope.class), //
    EMPLOYER_DEPARTMENT("application.id", DEPARTMENT, ApplicationByEmployingResourceScope.class);

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
