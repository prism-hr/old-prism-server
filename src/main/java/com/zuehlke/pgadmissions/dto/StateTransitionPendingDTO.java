package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class StateTransitionPendingDTO {

	private Integer id;

	private Integer resourceId;

	private PrismAction actionId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public final Integer getResourceId() {
		return resourceId;
	}

	public final void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public PrismAction getActionId() {
		return actionId;
	}

	public void setActionId(PrismAction actionId) {
		this.actionId = actionId;
	}

}
