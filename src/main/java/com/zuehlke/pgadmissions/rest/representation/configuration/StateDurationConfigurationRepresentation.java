package com.zuehlke.pgadmissions.rest.representation.configuration;

public class StateDurationConfigurationRepresentation extends WorkflowConfigurationRepresentation {

    private String label;
    
    private String tooltip;
    
    private Integer duration;

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
