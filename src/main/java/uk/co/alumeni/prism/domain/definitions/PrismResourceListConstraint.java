package uk.co.alumeni.prism.domain.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.LinkedHashMultimap;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.workflow.selectors.filter.PrismResourceListFilterSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ResourceByParentResourceSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ResourceByUserAndRoleSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.StateByStateGroupSelector;

public enum PrismResourceListConstraint {

    USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION)), //
    CODE("resource.code", PrismResourceListFilterPropertyType.STRING, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION)), //
    NAME("resource.name", PrismResourceListFilterPropertyType.STRING, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION)), //
    INSTITUTION_NAME("resource.institution.id", PrismResourceListFilterPropertyType.STRING, ResourceByParentResourceSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT)), //
    DEPARTMENT_NAME("resource.department.id", PrismResourceListFilterPropertyType.STRING, ResourceByParentResourceSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM)), //
    PROGRAM_NAME("resource.program.id", PrismResourceListFilterPropertyType.STRING, ResourceByParentResourceSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT)), //
    PROJECT_NAME("resource.project.id", PrismResourceListFilterPropertyType.STRING, ResourceByParentResourceSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION)), //
    STATE_GROUP_NAME("state.id", PrismResourceListFilterPropertyType.STATE_GROUP, StateByStateGroupSelector.class, Arrays.asList(PrismResourceListFilterExpression.EQUAL), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION)), //
    CREATED_TIMESTAMP("resource.createdTimestamp", PrismResourceListFilterPropertyType.DATE_TIME, StateByStateGroupSelector.class, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION)), //
    SUBMITTED_TIMESTAMP("resource.submittedTimestamp", PrismResourceListFilterPropertyType.DATE_TIME, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION)), //
    UPDATED_TIMESTAMP("resource.updatedTimestamp", PrismResourceListFilterPropertyType.DATE_TIME, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION)), //
    DUE_DATE("resource.dueDate", PrismResourceListFilterPropertyType.DATE, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER), //
            Arrays.asList(PrismScope.APPLICATION)), //
    CLOSING_DATE("resource.closingDate", PrismResourceListFilterPropertyType.DATE, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION)), //
    CONFIRMED_START_DATE("resource.confirmedStartDate", PrismResourceListFilterPropertyType.DATE, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION)), //
    RATING("resource.applicationRatingAverage", PrismResourceListFilterPropertyType.DECIMAL, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION)), //
    PROJECT_USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.PROJECT)), //
    PROGRAM_USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.PROGRAM)), //
    DEPARTMENT_USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.DEPARTMENT)), //
    INSTITUTION_USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.INSTITUTION));

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
