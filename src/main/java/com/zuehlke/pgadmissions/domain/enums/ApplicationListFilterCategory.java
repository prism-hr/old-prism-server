package com.zuehlke.pgadmissions.domain.enums;

import java.util.List;

import com.google.common.collect.Lists;

public enum ApplicationListFilterCategory {

    APPLICATION_NUMBER("Application number", CategoryType.TEXT), 
    APPLICANT_NAME("Applicant", CategoryType.TEXT), 
    PROGRAMME_NAME("Programme", CategoryType.TEXT), 
    PROJECT_TITLE("Project title", CategoryType.TEXT), 
    APPLICATION_STATUS("Status", CategoryType.TEXT), 
    SUBMISSION_DATE("Submission date", CategoryType.DATE), 
    SUPERVISOR("Supervisor", CategoryType.TEXT), 
    LAST_EDITED_DATE("Last edited date", CategoryType.DATE),
    CLOSING_DATE("Closing date", CategoryType.DATE);

    private final String displayValue;

    private final CategoryType type;

    private ApplicationListFilterCategory(String displayValue, CategoryType type) {
        this.displayValue = displayValue;
        this.type = type;
    }

    public String displayValue() {
        return displayValue;
    }
    
    public CategoryType getType() {
        return type;
    }

    public List<ResourceListSearchPredicate> getAvailablePredicates() {
        if (type == CategoryType.TEXT) {
            return Lists.newArrayList(ResourceListSearchPredicate.TEXT_CONTAINING, ResourceListSearchPredicate.TEXT_NOT_CONTAINING);
        } else if (type == CategoryType.DATE) {
            return Lists.newArrayList(ResourceListSearchPredicate.DATE_FROM, ResourceListSearchPredicate.DATE_ON, ResourceListSearchPredicate.DATE_TO);
        }
        throw new RuntimeException("Unknown predicate for following category: " + displayValue);
    }

    public static enum CategoryType {
        TEXT, 
        DATE
    }
}
