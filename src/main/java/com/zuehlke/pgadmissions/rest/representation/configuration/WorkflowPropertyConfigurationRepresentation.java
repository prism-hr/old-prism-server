package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory;

public class WorkflowPropertyConfigurationRepresentation extends WorkflowConfigurationVersionedRepresentation {

    private PrismWorkflowPropertyCategory category;

    private String label;

    private String tooltip;

    private Boolean enabled;

    private Integer minimum;

    private Integer maximum;

    public final PrismWorkflowPropertyCategory getCategory() {
        return category;
    }

    public final void setCategory(PrismWorkflowPropertyCategory category) {
        this.category = category;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final String getTooltip() {
        return tooltip;
    }

    public final void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public Integer getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

}
