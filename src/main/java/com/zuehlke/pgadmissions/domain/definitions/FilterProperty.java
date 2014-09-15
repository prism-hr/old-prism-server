package com.zuehlke.pgadmissions.domain.definitions;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.zuehlke.pgadmissions.domain.definitions.FilterValueType.*;

public enum FilterProperty {

    USER("user", Arrays.asList(FilterExpression.CONTAIN), STRING, Arrays.asList(PrismScope.APPLICATION)),
    CODE("code", Arrays.asList(FilterExpression.CONTAIN), STRING, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    INSTITUTION("institution", Arrays.asList(FilterExpression.CONTAIN), STRING, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM)),
    PROGRAM("program", Arrays.asList(FilterExpression.CONTAIN), STRING, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT)),
    PROJECT("project", Arrays.asList(FilterExpression.CONTAIN), STRING, Arrays.asList(PrismScope.APPLICATION)),
    STATE_GROUP("stateGroup", Arrays.asList(FilterExpression.EQUAL), FilterValueType.STATE_GROUP, Arrays.asList(PrismScope.APPLICATION)),
    CREATED_TIMESTAMP("createdTimestamp", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER),
            DATE, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    SUBMITTED_TIMESTAMP("submittedTimestamp",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            DATE, Arrays.asList(PrismScope.APPLICATION)),
    UPDATED_TIMESTAMP("updatedTimestamp",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            DATE, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    DUE_DATE("dueDate",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            DATE, Arrays.asList(PrismScope.APPLICATION)),
    CLOSING_DATE("closingDate",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            DATE, Arrays.asList(PrismScope.APPLICATION)),
    CONFIRMED_START_DATE("confirmed_start_date",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            DATE, Arrays.asList(PrismScope.APPLICATION)),
    RATING("applicationRatingAverage", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER),
            NUMBER, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    REFERRER("referrer",
            Arrays.asList(FilterExpression.CONTAIN),
            STRING, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    USER_ROLE("userRole", Arrays.asList(FilterExpression.CONTAIN), STRING, Arrays.asList(PrismScope.APPLICATION));

    private String propertyName;

    private List<FilterExpression> permittedExpressions;

    private FilterValueType valueType;

    private List<PrismScope> permittedScopes;

    private static final HashMultimap<PrismScope, FilterProperty> permittedFilterProperties = HashMultimap.create();

    static {
        for (FilterProperty property : FilterProperty.values()) {
            for (PrismScope scope : property.getPermittedScopes()) {
                permittedFilterProperties.put(scope, property);
            }
        }
    }

    private FilterProperty(String propertyName, List<FilterExpression> permittedExpressions, FilterValueType valueType, List<PrismScope> permittedScopes) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.valueType = valueType;
        this.permittedScopes = permittedScopes;
    }

    public final String getPropertyName() {
        return propertyName;
    }

    public final List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    public FilterValueType getValueType() {
        return valueType;
    }

    public final List<PrismScope> getPermittedScopes() {
        return permittedScopes;
    }

    public static final Set<FilterProperty> getPermittedFilterProperties(PrismScope scope) {
        return permittedFilterProperties.get(scope);
    }

    public static boolean isPermittedFilterProperty(PrismScope scope, FilterProperty property) {
        return FilterProperty.getPermittedFilterProperties(scope).contains(property);
    }

}
