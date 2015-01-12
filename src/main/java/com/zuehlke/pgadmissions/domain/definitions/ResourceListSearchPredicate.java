package com.zuehlke.pgadmissions.domain.definitions;

public enum ResourceListSearchPredicate {
    
    TEXT_CONTAINING("containing"), //
    TEXT_NOT_CONTAINING("not containing"),
    DATE_FROM("from"), //
    DATE_ON("on"), //
    DATE_TO("to");

    private final String displayValue;

    private ResourceListSearchPredicate(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }

}
