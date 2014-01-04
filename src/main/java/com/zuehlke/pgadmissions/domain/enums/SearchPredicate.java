package com.zuehlke.pgadmissions.domain.enums;

public enum SearchPredicate {

    CONTAINING("containing"),
    NOT_CONTAINING("not containing"),
    FROM_DATE("from"),
    ON_DATE("on"),
    TO_DATE("to");

    private final String displayValue;

    private SearchPredicate(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }

}