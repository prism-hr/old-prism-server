package com.zuehlke.pgadmissions.dto;

import java.util.Comparator;
import java.util.Set;

import com.google.common.collect.Sets;

public class ActionsDefinitions {

    private Set<ApplicationFormAction> actions = Sets.newTreeSet(ACTIONS_COMPARATOR);
    
    private boolean requiresAttention;

    public ActionsDefinitions() {
    }
    
    public ActionsDefinitions(final Set<ApplicationFormAction> actions, final boolean requiresAttention) {
        this.actions = actions;
        this.requiresAttention = requiresAttention;
    }

    public boolean isRequiresAttention() {
        return requiresAttention;
    }

    public void setRequiresAttention(boolean requiresAttention) {
        this.requiresAttention = requiresAttention;
    }
    
    public void addAction(ApplicationFormAction action){
        actions.add(action);
    }

    public Set<ApplicationFormAction> getActions() {
        return actions;
    }
    
    private static Comparator<ApplicationFormAction> ACTIONS_COMPARATOR = new Comparator<ApplicationFormAction>() {
        @Override
        public int compare(ApplicationFormAction o1, ApplicationFormAction o2) {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }
    }; 
    
}
