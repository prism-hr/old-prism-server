package uk.co.alumeni.prism.domain.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.workflow.selectors.filter.ApplicationByPrimaryLocationSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ApplicationByPrimaryThemeSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ApplicationBySecondaryLocationSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ApplicationBySecondaryThemeSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.PrismResourceListFilterSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ResourceByLocationSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ResourceByParentResourceSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ResourceByThemeSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.ResourceByUserAndRoleSelector;
import uk.co.alumeni.prism.workflow.selectors.filter.StateByStateGroupSelector;

import com.google.common.collect.LinkedHashMultimap;

public enum PrismResourceListConstraint implements PrismLocalizableDefinition {

    USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION), true), //
    CODE("resource.code", PrismResourceListFilterPropertyType.STRING, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION), true), //
    NAME("resource.name", PrismResourceListFilterPropertyType.STRING, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION), true), //
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
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION), true), //
    SUBMITTED_TIMESTAMP("resource.submittedTimestamp", PrismResourceListFilterPropertyType.DATE_TIME, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION), true), //
    UPDATED_TIMESTAMP("resource.updatedTimestamp", PrismResourceListFilterPropertyType.DATE_TIME, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION), true), //
    DUE_DATE("resource.dueDate", PrismResourceListFilterPropertyType.DATE, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER), //
            Arrays.asList(PrismScope.APPLICATION), true), //
    CLOSING_DATE("resource.closingDate", PrismResourceListFilterPropertyType.DATE, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION)), //
    CONFIRMED_START_DATE("resource.confirmedStartDate", PrismResourceListFilterPropertyType.DATE, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.EQUAL, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION)), //
    RATING("resource.applicationRatingAverage", PrismResourceListFilterPropertyType.DECIMAL, Arrays.asList(PrismResourceListFilterExpression.BETWEEN, PrismResourceListFilterExpression.GREATER, PrismResourceListFilterExpression.LESSER, PrismResourceListFilterExpression.NOT_SPECIFIED), //
            Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION), true), //
    PROJECT_USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.PROJECT), true), //
    PROGRAM_USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.PROGRAM), true), //
    DEPARTMENT_USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.DEPARTMENT), true), //
    INSTITUTION_USER("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.INSTITUTION), true), //
    THEME("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByThemeSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION), true), //
    PRIMARY_THEME("resource.id", PrismResourceListFilterPropertyType.STRING, ApplicationByPrimaryThemeSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION), true), //
    SECONDARY_THEME("resource.id", PrismResourceListFilterPropertyType.STRING, ApplicationBySecondaryThemeSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION), true), //
    LOCATION("resource.id", PrismResourceListFilterPropertyType.STRING, ResourceByLocationSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.DEPARTMENT, PrismScope.INSTITUTION), true),
    PRIMARY_LOCATION("resource.id", PrismResourceListFilterPropertyType.STRING, ApplicationByPrimaryLocationSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION), true),
    SECONDARY_LOCATION("resource.id", PrismResourceListFilterPropertyType.STRING, ApplicationBySecondaryLocationSelector.class, Arrays.asList(PrismResourceListFilterExpression.CONTAIN), //
            Arrays.asList(PrismScope.APPLICATION), true);

    private String propertyName;

    private PrismResourceListFilterPropertyType propertyType;

    private Class<? extends PrismResourceListFilterSelector<?>> propertyValueSelector;

    private List<PrismResourceListFilterExpression> permittedExpressions;

    private List<PrismScope> permittedScopes;
    
    private boolean permittedInBulkMode;

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
        this(propertyName, propertyType, permittedExpressions, permittedScopes, false);
    }
    
    private PrismResourceListConstraint(String propertyName, PrismResourceListFilterPropertyType propertyType, List<PrismResourceListFilterExpression> permittedExpressions,
            List<PrismScope> permittedScopes, boolean permittedInBulkMode) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.permittedExpressions = permittedExpressions;
        this.permittedScopes = permittedScopes;
        this.permittedInBulkMode = permittedInBulkMode;
    }

    private PrismResourceListConstraint(String propertyName, PrismResourceListFilterPropertyType propertyType,
            Class<? extends PrismResourceListFilterSelector<?>> propertyValueSelector, List<PrismResourceListFilterExpression> permittedExpressions,
            List<PrismScope> permittedScopes) {
        this(propertyName, propertyType, propertyValueSelector, permittedExpressions, permittedScopes, false);
    }
    
    private PrismResourceListConstraint(String propertyName, PrismResourceListFilterPropertyType propertyType,
            Class<? extends PrismResourceListFilterSelector<?>> propertyValueSelector, List<PrismResourceListFilterExpression> permittedExpressions,
            List<PrismScope> permittedScopes, boolean permittedInBulkMode) {
        this(propertyName, propertyType, permittedExpressions, permittedScopes, permittedInBulkMode);
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

    public boolean isPermittedInBulkMode() {
        return permittedInBulkMode;
    }
    
    @Override
    public PrismDisplayPropertyDefinition getDisplayProperty() {
        return PrismDisplayPropertyDefinition.valueOf("SYSTEM_FILTER_PROPERTY_" + name());
    }

}
