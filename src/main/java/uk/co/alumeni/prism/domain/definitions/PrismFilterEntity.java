package uk.co.alumeni.prism.domain.definitions;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.workflow.selectors.summary.ApplicationByEmployingResourceScope;
import uk.co.alumeni.prism.workflow.selectors.summary.ApplicationByQualifyingResourceScopeSelector;
import uk.co.alumeni.prism.workflow.selectors.summary.ApplicationByRejectionReasonSelector;
import uk.co.alumeni.prism.workflow.selectors.summary.PrismResourceSummarySelector;

public enum PrismFilterEntity {

    DOMICILE("application_personal_detail.domicile_id"), //
    NATIONALITY("application_personal_detail.nationality_id"), //
    AGE_RANGE("application_personal_detail.age_range_id"), //
    GENDER("application_personal_detail.gender_id"), //
    OPPORTUNITY_TYPE("application_program_detail.opportunity_type_id"), //
    REJECTION_REASON("application.id", ApplicationByRejectionReasonSelector.class), //
    STUDY_OPTION("application_program_detail.study_option_id"), //
    UNIVERSITY_INSTITUTION("application.id", PrismScope.INSTITUTION, ApplicationByQualifyingResourceScopeSelector.class), //
    UNIVERSITY_DEPARTMENT("application.id", PrismScope.DEPARTMENT, ApplicationByQualifyingResourceScopeSelector.class), //
    UNIVERSITY_PROGRAM("application.id", PrismScope.PROGRAM, ApplicationByQualifyingResourceScopeSelector.class), //
    UNIVERSITY_PROJECT("application.id", PrismScope.PROJECT, ApplicationByQualifyingResourceScopeSelector.class), //
    EMPLOYER_INSTITUTION("application.id", PrismScope.INSTITUTION, ApplicationByEmployingResourceScope.class), //
    EMPLOYER_DEPARTMENT("application.id", PrismScope.DEPARTMENT, ApplicationByEmployingResourceScope.class);

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
