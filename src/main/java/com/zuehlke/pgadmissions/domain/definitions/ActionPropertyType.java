package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Maps;

public enum ActionPropertyType {

    INPUT(String.class, "input", null), //
    TEXTAREA(String.class, "textArea", null), //
    SELECT_SINGLE(String.class, "selectSingle", null), //
    SELECT_MULTIPLE(String.class, "selectMultiple", null), //
    RATING_NORMAL(Integer.class, "ratingNormal", Arrays.asList((Object) 1, 2, 3, 4, 5)), //
    RATING_WEIGHTED(Integer.class, "ratingWeighted", Arrays.asList((Object) 1, 2, 3, 5, 8)), //
    DATE(LocalDate.class, "date", null), //
    DATE_RANGE(LocalDate.class, "dateRange", null), //
    DATE_TIME(DateTime.class, "dateTime", null), //
    DATE_TIME_RANGE(DateTime.class, "dateTimeRange", null);

    private Class<?> propertyClass;

    private String displayName;
    
    private List<Object> permittedValues;
    
    private static final HashMap<String, ActionPropertyType> displayNameIndex = Maps.newHashMap();
    
    static {
        for (ActionPropertyType value: values()) {
            displayNameIndex.put(value.getDisplayName(), value);
        }
    }

    private ActionPropertyType(Class<?> propertyClass, String displayName, List<Object> permittedValues) {
        this.propertyClass = propertyClass;
        this.displayName = displayName;
        this.permittedValues = permittedValues;
    }

    public final Class<?> getPropertyClass() {
        return propertyClass;
    }

    public final String getDisplayName() {
        return displayName;
    }
    
    public final List<Object> getPermittedValues() {
        return permittedValues;
    }

    public static final ActionPropertyType getByDisplayName(String displayName) {
        return displayNameIndex.get(displayName);
    }

}
