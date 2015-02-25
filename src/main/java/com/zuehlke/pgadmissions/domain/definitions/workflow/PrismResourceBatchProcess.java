package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import java.util.List;

import com.google.common.collect.Lists;

public enum PrismResourceBatchProcess {

	APPLICATION_SHORTLISTING_BATCH_PROCESS(APPLICATION, Lists.newArrayList(PROJECT, PROGRAM));

	private PrismScope scope;
	
	private List<PrismScope> batchScopes;

	private PrismResourceBatchProcess(PrismScope scope, List<PrismScope> batchScopes) {
		this.scope = scope;
		this.batchScopes = batchScopes;
	}	
	
	public PrismScope getScope() {
		return scope;
	}

	public List<PrismScope> getBatchScopes() {
		return batchScopes == null ? Lists.<PrismScope> newArrayList() : batchScopes;
	}

}
