package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyDefinition;

public class WorkflowPropertyConfigurationRepresentation extends WorkflowConfigurationVersionedRepresentation {

    private PrismWorkflowPropertyCategory category;

    private Boolean enabled;

    private Boolean required;

    private Integer minimum;

    private Integer maximum;

    public final PrismWorkflowPropertyCategory getCategory() {
        return category;
    }

    public final void setCategory(PrismWorkflowPropertyCategory category) {
        this.category = category;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
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

    public WorkflowPropertyConfigurationRepresentation withProperty(PrismWorkflowPropertyDefinition property) {
        setDefinitionId(property);
        return this;
    }

    public WorkflowPropertyConfigurationRepresentation withCategory(PrismWorkflowPropertyCategory category) {
        this.category = category;
        return this;
    }

    public WorkflowPropertyConfigurationRepresentation withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public WorkflowPropertyConfigurationRepresentation withRequired(Boolean required) {
        this.required = required;
        return this;
    }

    public WorkflowPropertyConfigurationRepresentation withMinimum(Integer minimum) {
        this.minimum = minimum;
        return this;
    }

    public WorkflowPropertyConfigurationRepresentation withMaximum(Integer maximum) {
        this.maximum = maximum;
        return this;
    }

    public WorkflowPropertyConfigurationRepresentation withVersion(Integer version) {
        setVersion(version);
        return this;
    }

}
