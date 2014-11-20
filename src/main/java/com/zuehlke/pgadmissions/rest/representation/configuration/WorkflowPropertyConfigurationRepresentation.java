package com.zuehlke.pgadmissions.rest.representation.configuration;

public class WorkflowPropertyConfigurationRepresentation extends WorkflowConfigurationVersionedRepresentation {
    
    private Boolean enabled;

    private Integer minimum;

    private Integer maximum;

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
