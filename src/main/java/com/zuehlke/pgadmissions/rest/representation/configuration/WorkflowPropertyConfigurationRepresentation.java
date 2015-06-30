package com.zuehlke.pgadmissions.rest.representation.configuration;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismWorkflowPropertyCategory;

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

}
