package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.google.common.collect.Maps;

public enum PrismCustomQuestionType {

    INPUT(String.class, "input", null), //
    TEXT_AREA(String.class, "textArea", null), //
    SELECT_SINGLE(String.class, "selectSingle", null), //
    SELECT_MULTIPLE(String.class, "selectMultiple", null), //
    RATING_NORMAL(Integer.class, "ratingNormal", Arrays.asList((Object) 1, 2, 3, 4, 5)), //
    RATING_WEIGHTED(Integer.class, "ratingWeighted", Arrays.asList((Object) 1, 2, 3, 5, 8)), //
    DATE(LocalDate.class, "date", null), //
    DATE_RANGE(LocalDate.class, "dateRange", null), //
    DATE_TIME(DateTime.class, "dateTime", null), //
    DATE_TIME_RANGE(DateTime.class, "dateTimeRange", null);

    private Class<?> propertyClass;

    private String componentName;

    private List<Object> permittedValues;

    private static final HashMap<String, PrismCustomQuestionType> componentNameIndex = Maps.newHashMap();

    static {
        for (PrismCustomQuestionType value: values()) {
            componentNameIndex.put(value.getComponentName(), value);
        }
    }

    private PrismCustomQuestionType(Class<?> propertyClass, String componentName, List<Object> permittedValues) {
        this.propertyClass = propertyClass;
        this.componentName = componentName;
        this.permittedValues = permittedValues;
    }

    public final Class<?> getPropertyClass() {
        return propertyClass;
    }

    public final String getComponentName() {
        return componentName;
    }

    public final List<Object> getPermittedValues() {
        return permittedValues;
    }

    public static final PrismCustomQuestionType getByComponentName(String displayName) {
        return componentNameIndex.get(displayName);
    }

}
