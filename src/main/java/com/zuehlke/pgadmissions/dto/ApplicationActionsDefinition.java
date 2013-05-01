package com.zuehlke.pgadmissions.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApplicationActionsDefinition {

    private Map<String, String> actions = new LinkedHashMap<String, String>();
    
    private boolean requiresAttention;

    public boolean isRequiresAttention() {
        return requiresAttention;
    }

    public void setRequiresAttention(boolean requiresAttention) {
        this.requiresAttention = requiresAttention;
    }
    
    public void addAction(String name, String displayValue){
        actions.put(name, displayValue);
    }

    public Map<String, String> getActions() {
        return actions;
    }
    
    
    
}
