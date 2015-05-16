package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.BETWEEN;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.CONTAIN;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.EQUAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.GREATER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.LESSER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.NOT_SPECIFIED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.APPLICATION_RESERVE_STATUS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.DATE_TIME;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.DECIMAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.STRING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.workflow.selectors.filter.PrismResourceListFilterSelector;
import com.zuehlke.pgadmissions.workflow.selectors.filter.ResourceByParentResourceSelector;
import com.zuehlke.pgadmissions.workflow.selectors.filter.ResourceByPartnerSelector;
import com.zuehlke.pgadmissions.workflow.selectors.filter.ResourceBySponsorSelector;
import com.zuehlke.pgadmissions.workflow.selectors.filter.ResourceByUserAndRoleSelector;
import com.zuehlke.pgadmissions.workflow.selectors.filter.StateByStateGroupSelector;

public enum PrismResourceListFilter {

    USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION)), //
    CODE("code", STRING, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    TITLE("title", STRING, Arrays.asList(CONTAIN), //
            Arrays.asList(PROJECT, PROGRAM, INSTITUTION)), //
    INSTITUTION_TITLE("institution.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM)), //
    PARTNER_TITLE("id", STRING, ResourceByPartnerSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM)), //
    SPONSOR_TITLE("id", STRING, ResourceBySponsorSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM)), //
    PROGRAM_TITLE("program.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT)), //
    PROJECT_TITLE("project.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    STATE_GROUP_TITLE("state.id", STATE_GROUP, StateByStateGroupSelector.class, Arrays.asList(EQUAL), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    CREATED_TIMESTAMP("createdTimestamp", DATE_TIME, StateByStateGroupSelector.class, Arrays.asList(BETWEEN, GREATER, LESSER), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    SUBMITTED_TIMESTAMP("submittedTimestamp", DATE_TIME, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    UPDATED_TIMESTAMP("updatedTimestamp", DATE_TIME, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    DUE_DATE("dueDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), //
            Arrays.asList(APPLICATION)), //
    CLOSING_DATE("closingDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    STUDY_LOCATION("studyDetail.studyLocation", STRING, Arrays.asList(CONTAIN, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    STUDY_DIVISION("studyDetail.studyDivision", STRING, Arrays.asList(CONTAIN, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    STUDY_AREA("studyDetail.studyArea", STRING, Arrays.asList(CONTAIN, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    STUDY_APPLICATION("studyDetail.studyApplicationId", STRING, Arrays.asList(CONTAIN, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    CONFIRMED_START_DATE("confirmedStartDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    RATING("applicationRatingAverage", DECIMAL, Arrays.asList(BETWEEN, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    REFERRER("referrer", STRING, Arrays.asList(CONTAIN, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, INSTITUTION)), //
    SUPERVISOR("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION)), //
    PROJECT_USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(PROJECT)), //
    PROGRAM_USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(PROGRAM)), //
    INSTITUTION_USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(INSTITUTION)), //
    PRIMARY_THEME("primaryTheme", STRING, Arrays.asList(CONTAIN, EQUAL, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    SECONDARY_THEME("secondaryTheme", STRING, Arrays.asList(CONTAIN, EQUAL, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    RESERVE_STATUS("applicationReserveStatus", APPLICATION_RESERVE_STATUS, Arrays.asList(EQUAL, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION));

    private String propertyName;

    private PrismResourceListFilterPropertyType propertyType;

    private Class<? extends PrismResourceListFilterSelector<?>> propertyValueSelector;

    private List<PrismResourceListFilterExpression> permittedExpressions;

    private List<PrismScope> permittedScopes;

    private static LinkedHashMultimap<PrismScope, PrismResourceListFilter> permittedFilters = LinkedHashMultimap.create();

    static {
        for (PrismResourceListFilter filter : values()) {
            for (PrismScope scope : filter.getPermittedScopes()) {
                permittedFilters.put(scope, filter);
            }
        }
    }

    private PrismResourceListFilter(String propertyName, PrismResourceListFilterPropertyType propertyType,
            List<PrismResourceListFilterExpression> permittedExpressions,
            List<PrismScope> permittedScopes) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.permittedExpressions = permittedExpressions;
        this.permittedScopes = permittedScopes;
    }

    private PrismResourceListFilter(String propertyName, PrismResourceListFilterPropertyType propertyType,
            Class<? extends PrismResourceListFilterSelector<?>> propertyValueSelector,
            List<PrismResourceListFilterExpression> permittedExpressions, List<PrismScope> permittedScopes) {
        this(propertyName, propertyType, permittedExpressions, permittedScopes);
        this.propertyValueSelector = propertyValueSelector;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public PrismResourceListFilterPropertyType getPropertyType() {
        return propertyType;
    }

    public Class<? extends PrismResourceListFilterSelector<?>> getPropertyValueSelector() {
        return propertyValueSelector;
    }

    public static Set<PrismResourceListFilter> getPermittedFilters(PrismScope scope) {
        return permittedFilters.get(scope);
    }

    public List<PrismResourceListFilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    public List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }

}
