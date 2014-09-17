package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum FilterProperty {

    USER("user.id", Arrays.asList(FilterExpression.CONTAIN), FilterPropertyType.STRING, Arrays.asList(PrismScope.APPLICATION)),
    CODE("code", Arrays.asList(FilterExpression.CONTAIN), FilterPropertyType.STRING, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    INSTITUTION("institution.id", Arrays.asList(FilterExpression.CONTAIN), FilterPropertyType.STRING, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM)),
    PROGRAM("program.id", Arrays.asList(FilterExpression.CONTAIN), FilterPropertyType.STRING, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT)),
    PROJECT("project.id", Arrays.asList(FilterExpression.CONTAIN), FilterPropertyType.STRING, Arrays.asList(PrismScope.APPLICATION)),
    TITLE("title", Arrays.asList(FilterExpression.CONTAIN), FilterPropertyType.STRING, Arrays.asList(PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    STATE_GROUP("state.id", Arrays.asList(FilterExpression.EQUAL), FilterPropertyType.STATE_GROUP, Arrays.asList(PrismScope.APPLICATION)),
    CREATED_TIMESTAMP("createdTimestamp", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER),
            FilterPropertyType.DATE, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    SUBMITTED_TIMESTAMP("submittedTimestamp",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            FilterPropertyType.DATE, Arrays.asList(PrismScope.APPLICATION)),
    UPDATED_TIMESTAMP("updatedTimestamp",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            FilterPropertyType.DATE, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    DUE_DATE("dueDate",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            FilterPropertyType.DATE, Arrays.asList(PrismScope.APPLICATION)),
    CLOSING_DATE("closingDate",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            FilterPropertyType.DATE, Arrays.asList(PrismScope.APPLICATION)),
    CONFIRMED_START_DATE("confirmed_start_date",
            Arrays.asList(FilterExpression.BETWEEN, FilterExpression.EQUAL, FilterExpression.GREATER, FilterExpression.LESSER),
            FilterPropertyType.DATE, Arrays.asList(PrismScope.APPLICATION)),
    RATING("applicationRatingAverage", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER),
            FilterPropertyType.NUMBER, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    REFERRER("referrer",
            Arrays.asList(FilterExpression.CONTAIN),
            FilterPropertyType.STRING, Arrays.asList(PrismScope.APPLICATION, PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION)),
    SUPERVISOR("id", Arrays.asList(FilterExpression.CONTAIN), FilterPropertyType.STRING, Arrays.asList(PrismScope.APPLICATION));

    private String propertyName;

    private FilterPropertyType propertyType;
    
    private List<FilterExpression> permittedExpressions;

    private List<PrismScope> permittedScopes;

    private static final HashMultimap<PrismScope, FilterProperty> permittedFilterProperties = HashMultimap.create();

    static {
        for (FilterProperty property : FilterProperty.values()) {
            for (PrismScope scope : property.getPermittedScopes()) {
                permittedFilterProperties.put(scope, property);
            }
        }
    }

    private FilterProperty(String propertyName, List<FilterExpression> permittedExpressions, FilterPropertyType valueType, List<PrismScope> permittedScopes) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.propertyType = valueType;
        this.permittedScopes = permittedScopes;
    }

    public final String getPropertyName() {
        return propertyName;
    }

    public final List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }

    public FilterPropertyType getPropertyType() {
        return propertyType;
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
