package com.zuehlke.pgadmissions.domain.definitions;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public enum ActionPropertyType {

    INPUT(String.class, "input", null), //
    TEXTAREA(String.class, "textarea", null), //
    SELECT_SINGLE(String.class, "select_single", null), //
    SELECT_MULTIPLE(String.class, "select_multiple", null), //
    RATING_NORMAL(Integer.class, "rating_normal", Arrays.asList((Object) 1, 2, 3, 4, 5)), //
    RATING_WEIGHTED(Integer.class, "rating_weighted", Arrays.asList((Object) 1, 2, 3, 5, 8)), //
    DATE(LocalDate.class, "date", null), //
    DATE_RANGE(LocalDate.class, "date_range", null), //
    DATE_TIME(DateTime.class, "date_time", null), //
    DATE_TIME_RANGE(DateTime.class, "date_time_range", null);

    private Class<?> type;

    private String name;

    private List<Object> options;

    private ActionPropertyType(Class<?> type, String name, List<Object> options) {
        this.type = type;
        this.name = name;
        this.options = options;
    }

    public final Class<?> getType() {
        return type;
    }

    public final String getName() {
        return name;
    }

    public final List<Object> getOptions() {
        return options;
    }

}
