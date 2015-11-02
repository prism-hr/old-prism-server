package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByEmployingResourceScope;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByQualifyingResourceScopeSelector;
import com.zuehlke.pgadmissions.workflow.selectors.summary.ApplicationByRejectionReasonSelector;
import com.zuehlke.pgadmissions.workflow.selectors.summary.PrismResourceSummarySelector;

public enum PrismFilterEntity {

    DOMICILE("application_personal_detail.domicile_id"), //
    NATIONALITY("application_personal_detail.nationality_id"), //
    AGE_RANGE("application_personal_detail.age_range_id"), //
    GENDER("application_personal_detail.gender_id"), //
    OPPORTUNITY_TYPE("application_program_detail.opportunity_type_id"), //
    REJECTION_REASON("application.id", ApplicationByRejectionReasonSelector.class), //
    STUDY_OPTION("application_program_detail.study_option_id"), //
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
