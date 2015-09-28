package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.BETWEEN;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.CONTAIN;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.EQUAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.GREATER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.LESSER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterExpression.NOT_SPECIFIED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.DATE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.DATE_TIME;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.DECIMAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.STATE_GROUP;
import static com.zuehlke.pgadmissions.domain.definitions.PrismResourceListFilterPropertyType.STRING;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
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
import com.zuehlke.pgadmissions.workflow.selectors.filter.ResourceByUserAndRoleSelector;
import com.zuehlke.pgadmissions.workflow.selectors.filter.StateByStateGroupSelector;

public enum PrismResourceListConstraint {

    USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION)), //
    CODE("code", STRING, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION)), //
    NAME("name", STRING, Arrays.asList(CONTAIN), //
            Arrays.asList(PROJECT, PROGRAM, DEPARTMENT, INSTITUTION)), //
    INSTITUTION_NAME("institution.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT)), //
    DEPARTMENT_NAME("department.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM)), //
    PROGRAM_NAME("program.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT)), //
    PROJECT_NAME("project.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    STATE_GROUP_NAME("state.id", STATE_GROUP, StateByStateGroupSelector.class, Arrays.asList(EQUAL), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION)), //
    CREATED_TIMESTAMP("createdTimestamp", DATE_TIME, StateByStateGroupSelector.class, Arrays.asList(BETWEEN, GREATER, LESSER), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION)), //
    SUBMITTED_TIMESTAMP("submittedTimestamp", DATE_TIME, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    UPDATED_TIMESTAMP("updatedTimestamp", DATE_TIME, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION)), //
    DUE_DATE("dueDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), //
            Arrays.asList(APPLICATION)), //
    CLOSING_DATE("closingDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    CONFIRMED_START_DATE("confirmedStartDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    RATING("applicationRatingAverage", DECIMAL, Arrays.asList(BETWEEN, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION)), //
    PROJECT_USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(PROJECT)), //
    PROGRAM_USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(PROGRAM)), //
    DEPARTMENT_USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(DEPARTMENT)), //
    INSTITUTION_USER("id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(INSTITUTION));
    
    private String propertyName;

    private PrismResourceListFilterPropertyType propertyType;

    private Class<? extends PrismResourceListFilterSelector<?>> propertyValueSelector;

    private List<PrismResourceListFilterExpression> permittedExpressions;

    private List<PrismScope> permittedScopes;

    private static LinkedHashMultimap<PrismScope, PrismResourceListConstraint> permittedFilters = LinkedHashMultimap.create();

    static {
        for (PrismResourceListConstraint filter : values()) {
            for (PrismScope scope : filter.getPermittedScopes()) {
                permittedFilters.put(scope, filter);
            }
        }
    }

    private PrismResourceListConstraint(String propertyName, PrismResourceListFilterPropertyType propertyType, List<PrismResourceListFilterExpression> permittedExpressions,
            List<PrismScope> permittedScopes) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.permittedExpressions = permittedExpressions;
        this.permittedScopes = permittedScopes;
    }

    private PrismResourceListConstraint(String propertyName, PrismResourceListFilterPropertyType propertyType,
            Class<? extends PrismResourceListFilterSelector<?>> propertyValueSelector, List<PrismResourceListFilterExpression> permittedExpressions,
            List<PrismScope> permittedScopes) {
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

    public static Set<PrismResourceListConstraint> getPermittedFilters(PrismScope scope) {
        return permittedFilters.get(scope);
    }

    public List<PrismResourceListFilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    public List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }

}
