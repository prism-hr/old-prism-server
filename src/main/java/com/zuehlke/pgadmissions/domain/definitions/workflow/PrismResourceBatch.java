package com.zuehlke.pgadmissions.domain.definitions.workflow;

import java.util.List;

import com.google.common.collect.Lists;

public class PrismResourceBatch {

	private PrismResourceBatchType resourceBatchType;

	private List<PrismRole> roleAssignments = Lists.newArrayList();

	public PrismResourceBatchType getResourceBatchType() {
		return resourceBatchType;
	}

	public List<PrismRole> getRoleAssignments() {
		return roleAssignments;
	}

	public PrismResourceBatch withResourceBatchType(PrismResourceBatchType resourceBatchType) {
		this.resourceBatchType = resourceBatchType;
		return this;
	}

	public PrismResourceBatch withRoleAssignments(List<PrismRole> roleAssignments) {
		this.roleAssignments = roleAssignments == null ? this.roleAssignments : roleAssignments;
		return this;
	}

}
