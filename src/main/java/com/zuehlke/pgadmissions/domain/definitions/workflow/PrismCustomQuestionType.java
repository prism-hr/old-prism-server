package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;

public enum PrismCustomQuestionType {

    INPUT("textInput", null), //
    TEXT_AREA("textArea", null), //
    RADIO("radio", null), //
    SELECT_SINGLE("selectSingle", null), //
    SELECT_MULTIPLE("selectMultiple", null), //
    RATING_NORMAL("ratingNormal", Arrays.asList("1", "2", "3", "4", "5")), //
    RATING_WEIGHTED("ratingWeighted", Arrays.asList("1", "2", "3", "5", "8")), //
    DATE("date", null), //
    DATE_RANGE("dateRange", null), //
    DATE_TIME("dateTime", null), //
    DATE_TIME_RANGE("dateTimeRange", null);

    private String componentName;

    private List<String> permittedValues;

    private static final HashMap<String, PrismCustomQuestionType> componentNameIndex = Maps.newHashMap();

    static {
        for (PrismCustomQuestionType value: values()) {
            componentNameIndex.put(value.getComponentName(), value);
        }
    }

    private PrismCustomQuestionType(String componentName, List<String> permittedValues) {
        this.componentName = componentName;
        this.permittedValues = permittedValues;
    }

    public final String getComponentName() {
        return componentName;
    }

    public final List<String> getPermittedValues() {
        return permittedValues;
    }

    public static final PrismCustomQuestionType getByComponentName(String displayName) {
        return componentNameIndex.get(displayName);
    }

}
