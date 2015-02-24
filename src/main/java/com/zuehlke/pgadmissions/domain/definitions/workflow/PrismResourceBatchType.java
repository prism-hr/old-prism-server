package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

public enum PrismResourceBatchType {

	APPLICATION_SHORTLISTING(APPLICATION);

	private PrismScope scope;

	private PrismResourceBatchType(PrismScope scope) {
		this.scope = scope;
	}

	public PrismScope getScope() {
		return scope;
	}

}
