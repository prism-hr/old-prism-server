package uk.co.alumeni.prism.domain.definitions;

import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression.BETWEEN;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression.CONTAIN;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression.EQUAL;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression.GREATER;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression.LESSER;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterExpression.NOT_SPECIFIED;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterPropertyType.DATE;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterPropertyType.DATE_TIME;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterPropertyType.DECIMAL;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterPropertyType.STATE_GROUP;
import static uk.co.alumeni.prism.domain.definitions.PrismResourceListFilterPropertyType.STRING;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;

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

    USER("resource.id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION), true), //
    CODE("resource.code", STRING, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION), true), //
    NAME("resource.name", STRING, Arrays.asList(CONTAIN), //
            Arrays.asList(PROJECT, PROGRAM, DEPARTMENT, INSTITUTION), true), //
    INSTITUTION_NAME("resource.institution.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT)), //
    DEPARTMENT_NAME("resource.department.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM)), //
    PROGRAM_NAME("resource.program.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(APPLICATION, PROJECT)), //
    PROJECT_NAME("resource.project.id", STRING, ResourceByParentResourceSelector.class, Arrays.asList(CONTAIN, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    STATE_GROUP_NAME("state.id", STATE_GROUP, StateByStateGroupSelector.class, Arrays.asList(EQUAL), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION)), //
    CREATED_TIMESTAMP("resource.createdTimestamp", DATE_TIME, StateByStateGroupSelector.class, Arrays.asList(BETWEEN, GREATER, LESSER), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION), true), //
    SUBMITTED_TIMESTAMP("resource.submittedTimestamp", DATE_TIME, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION), true), //
    UPDATED_TIMESTAMP("resource.updatedTimestamp", DATE_TIME, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION), true), //
    DUE_DATE("resource.dueDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER), //
            Arrays.asList(APPLICATION), true), //
    CLOSING_DATE("resource.closingDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    CONFIRMED_START_DATE("resource.confirmedStartDate", DATE, Arrays.asList(BETWEEN, EQUAL, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION)), //
    RATING("resource.applicationRatingAverage", DECIMAL, Arrays.asList(BETWEEN, GREATER, LESSER, NOT_SPECIFIED), //
            Arrays.asList(APPLICATION, PROJECT, PROGRAM, DEPARTMENT, INSTITUTION), true), //
    PROJECT_USER("resource.id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(PROJECT), true), //
    PROGRAM_USER("resource.id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(PROGRAM), true), //
    DEPARTMENT_USER("resource.id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(DEPARTMENT), true), //
    INSTITUTION_USER("resource.id", STRING, ResourceByUserAndRoleSelector.class, Arrays.asList(CONTAIN), //
            Arrays.asList(INSTITUTION), true), //
    THEME("resource.id", STRING, ResourceByThemeSelector.class, Arrays.asList(CONTAIN, EQUAL), //
            Arrays.asList(PROJECT, PROGRAM, DEPARTMENT, INSTITUTION), true), //
    PRIMARY_THEME("resource.id", STRING, ApplicationByPrimaryThemeSelector.class, Arrays.asList(CONTAIN, EQUAL), //
            Arrays.asList(APPLICATION), true), //
    SECONDARY_THEME("resource.id", STRING, ApplicationBySecondaryThemeSelector.class, Arrays.asList(CONTAIN, EQUAL), //
            Arrays.asList(APPLICATION), true), //
    LOCATION("resource.id", STRING, ResourceByLocationSelector.class, Arrays.asList(CONTAIN, EQUAL), //
            Arrays.asList(PROJECT, PROGRAM, DEPARTMENT, INSTITUTION), true),
    PRIMARY_LOCATION("resource.id", STRING, ApplicationByPrimaryLocationSelector.class, Arrays.asList(CONTAIN, EQUAL), //
            Arrays.asList(APPLICATION), true),
    SECONDARY_LOCATION("resource.id", STRING, ApplicationBySecondaryLocationSelector.class, Arrays.asList(CONTAIN, EQUAL), //
            Arrays.asList(APPLICATION), true);

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
