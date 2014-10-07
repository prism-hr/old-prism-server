package com.zuehlke.pgadmissions.domain.definitions;

public enum PrismDisplayProperty {

    DATE_FORMAT(PrismDisplayCategory.GLOBAL, "dd MMM yyyy");
    
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
