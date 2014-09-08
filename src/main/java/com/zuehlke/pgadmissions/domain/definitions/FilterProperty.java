package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.AbstractFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DateFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.DecimalFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StateGroupFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.StringFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO.UserRoleFilterDTO;

public enum FilterProperty {

    USER("user", Arrays.asList(FilterExpression.CONTAIN), StringFilterDTO.class),
    CODE("code", Arrays.asList(FilterExpression.CONTAIN), StringFilterDTO.class),
    INSTITUTION("institution", Arrays.asList(FilterExpression.CONTAIN), StringFilterDTO.class),
    PROGRAM("program", Arrays.asList(FilterExpression.CONTAIN), StringFilterDTO.class),
    PROJECT("project", Arrays.asList(FilterExpression.CONTAIN), StringFilterDTO.class),
    CREATED_TIMESTAMP("createdTimestamp", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER), DateFilterDTO.class),
    SUBMITTED_TIMESTAMP("submittedTimestamp", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER), DateFilterDTO.class),
    UPDATED_TIMESTAMP("updatedTimestamp", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER), DateFilterDTO.class),
    DUE_DATE("dueDate", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER), DateFilterDTO.class),
    CLOSING_DATE("closingDate", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER), DateFilterDTO.class),
    STATE_GROUP("stateGroup", Arrays.asList(FilterExpression.EQUAL), StateGroupFilterDTO.class),
    REFERRER("referrer", Arrays.asList(FilterExpression.CONTAIN), StringFilterDTO.class),
    USER_ROLE("userRole", Arrays.asList(FilterExpression.CONTAIN), UserRoleFilterDTO.class),
    CONFIRMED_START_DATE("confirmed_start_date", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER), DateFilterDTO.class),
    RATING("rating", Arrays.asList(FilterExpression.BETWEEN, FilterExpression.GREATER, FilterExpression.LESSER), DecimalFilterDTO.class);
    
    private String propertyName;
    
    private List<FilterExpression> permittedExpressions;
    
    private Class<? extends AbstractFilterDTO> filterClass;
    
    private static HashMap<String, FilterProperty> propertyNameIndex = Maps.newHashMap();
    
    static {
        for (FilterProperty property : FilterProperty.values()) {
            propertyNameIndex.put(property.getPropertyName(), property);
        }
    }
    
    private FilterProperty(String propertyName, List<FilterExpression> permittedExpressions, Class<? extends AbstractFilterDTO> filterClass) {
        this.propertyName = propertyName;
        this.permittedExpressions = permittedExpressions;
        this.filterClass = filterClass;
    }

    public final String getPropertyName() {
        return propertyName;
    }
    
    public final List<FilterExpression> getPermittedExpressions() {
        return permittedExpressions;
    }
    
    public final Class<? extends AbstractFilterDTO> getFilterClass() {
        return filterClass;
    }

    public static FilterProperty getByPropertyName(String propertyName) {
        if (propertyNameIndex.containsKey(propertyName)) {
            return propertyNameIndex.get(propertyName);
        }
        throw new Error("Invalid resource list filter property: " + propertyName);
    }
    
}
