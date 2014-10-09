package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismDisplayProperty {

    SYSTEM_RUNNING_DATE_FORMAT(PrismDisplayCategory.SYSTEM_RUNNING, "dd MMM yyyy"),
    APPLICATION_REJECTED_SYSTEM(PrismDisplayCategory.APPLICATION_REJECTED, "The opportunity that you applied for has been discontinued.");
    
    private PrismDisplayCategory category;
    
    private String defaultValue;
    
    private PrismDisplayProperty(PrismDisplayCategory category, String defaultValue) {
        this.category = category;
        this.defaultValue = defaultValue;
    }

    public final PrismDisplayCategory getCategory() {
        return category;
    }

    public final String getDefaultValue() {
        return defaultValue;
    }
    
}
