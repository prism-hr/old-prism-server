package com.zuehlke.pgadmissions.domain.enums;

import java.util.List;

import com.google.common.collect.Lists;

public enum SearchCategory {

    APPLICATION_NUMBER("Application number", CategoryType.TEXT), APPLICANT_NAME("Applicant", CategoryType.TEXT), PROGRAMME_NAME("Programme", CategoryType.TEXT), APPLICATION_STATUS(
            "Status", CategoryType.TEXT), APPLICATION_DATE("Date", CategoryType.DATE);

    private final String displayValue;

    private final CategoryType type;

    private SearchCategory(String displayValue, CategoryType type) {
        this.displayValue = displayValue;
        this.type = type;
    }

    public String displayValue() {
        return displayValue;
    }

    public List<SearchPredicate> getAvailablePredicates() {
        if (type == CategoryType.TEXT) {
            return Lists.newArrayList(SearchPredicate.CONTAINING, SearchPredicate.NOT_CONTAINING);
        } else if (type == CategoryType.DATE) {
            return Lists.newArrayList(SearchPredicate.FROM_DATE, SearchPredicate.ON_DATE, SearchPredicate.TO_DATE);
        }
        throw new RuntimeException();
    }

    private static enum CategoryType {
        TEXT, DATE
    }
}
