package com.zuehlke.pgadmissions.dto;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

public class ActionsDefinitions {

    private Set<ApplicationFormAction> actions = Sets.newTreeSet(ACTIONS_COMPARATOR);

    private Set<ApplicationFormAction> actionsRequiringAttention = new HashSet<ApplicationFormAction>();

    public ActionsDefinitions() {
    }

    public ActionsDefinitions(final Set<ApplicationFormAction> actions, final Set<ApplicationFormAction> actionsRequireAttention) {
        this.actions = actions;
        this.actionsRequiringAttention = actionsRequireAttention;
    }

    public boolean isRequiresAttention() {
        return !actionsRequiringAttention.isEmpty();
    }

    public void addAction(ApplicationFormAction action) {
        actions.add(action);
    }

    public void addActionRequiringAttention(ApplicationFormAction action) {
        actionsRequiringAttention.add(action);
    }

    public Set<ApplicationFormAction> getActions() {
        return actions;
    }

    public Set<ApplicationFormAction> getActionsRequiringAttention() {
        return actionsRequiringAttention;
    }

    private static Comparator<ApplicationFormAction> ACTIONS_COMPARATOR = new Comparator<ApplicationFormAction>() {
        @Override
        public int compare(ApplicationFormAction o1, ApplicationFormAction o2) {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
        }
    };

}
