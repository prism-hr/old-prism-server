package com.zuehlke.pgadmissions.domain.definitions.workflow;

public enum PrismWorkflowConfiguration {
    
    PASSPORT_LIMIT(1, 1);
    
    private Integer defaultMinimumRequired;
    
    private Integer defaultMaximumRequired;

    private PrismWorkflowConfiguration(Integer defaultMinimumRequired, Integer defaultMaximumRequired) {
        this.defaultMinimumRequired = defaultMinimumRequired;
        this.defaultMaximumRequired = defaultMaximumRequired;
    }

    public final Integer getDefaultMinimumRequired() {
        return defaultMinimumRequired;
    }

    public final Integer getDefaultMaximumRequired() {
        return defaultMaximumRequired;
    }
    
}
