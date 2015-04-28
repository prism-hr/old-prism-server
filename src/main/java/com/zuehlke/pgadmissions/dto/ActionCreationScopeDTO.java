package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.Scope;

public class ActionCreationScopeDTO {

	private Action action;

	private Scope creationScope;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Scope getCreationScope() {
		return creationScope;
	}

	public void setCreationScope(Scope creationScope) {
		this.creationScope = creationScope;
	}

}
