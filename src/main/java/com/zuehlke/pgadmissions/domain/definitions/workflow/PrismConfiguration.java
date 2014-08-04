package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismConfiguration {
    
    DAY_ACTION_EXPIRY_DURATION(56);
    
    private Integer defaultValue;
    
    private PrismConfiguration(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }
    
}
