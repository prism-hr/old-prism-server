package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Arrays;
import java.util.List;

public enum FilterProperty {

    USER("user", Arrays.asList(FilterExpression.CONTAIN)),
    CODE("code", Arrays.asList(FilterExpression.CONTAIN)),
    INSTITUTION("institution", Arrays.asList(FilterExpression.CONTAIN)),
    PROGRAM("program", Arrays.asList(FilterExpression.CONTAIN)),
    PROJECT("project", Arrays.asList(FilterExpression.CONTAIN)),
    CREATED_TIMESTAMP("createdTimestamp", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER)),
    SUBMITTED_TIMESTAMP("submittedTimestamp", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER)),
    UPDATED_TIMESTAMP("updatedTimestamp", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER)),
    DUE_DATE("dueDate", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER)),
    CLOSING_DATE("closingDate", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER)),
    STATE_GROUP("stateGroup", Arrays.asList(FilterExpression.EQUAL)),
    REFERRER("referrer", Arrays.asList(FilterExpression.CONTAIN)),
    USER_ROLE("userRole", Arrays.asList(FilterExpression.CONTAIN)),
    CONFIRMED_START_DATE("confirmed_start_date", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER)),
    RATING("rating", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER));
    
    private String propertyName;
    
    private List<FilterExpression> permittedExpressions;
    
    private FilterProperty(String propertyName, List<FilterExpression> permittedExpressions) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
    }

    public final String getPropertyName() {
        return propertyName;
    }
    
    public final List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }
    
}
