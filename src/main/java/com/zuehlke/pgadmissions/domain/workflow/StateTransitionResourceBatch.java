package com.zuehlke.pgadmissions.domain.workflow;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismResourceBatchType;

@Entity
@Table(name = "STATE_TRANISTION_RESOURCE_BATCH")
public class StateTransitionResourceBatch extends WorkflowDefinition {

	@Id
	@Column(name = "id", nullable = false)
	@Enumerated(EnumType.STRING)
	private PrismResourceBatchType id;

	@ManyToOne
	@JoinColumn(name = "scope_id", nullable = false)
	private Scope scope;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "STATE_TRANSITION_RESOURCE_BATCH_ROLE_ASSIGNMENT", joinColumns = { @JoinColumn(name = "state_transition_resource_batch_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "role_id", nullable = false) })
	private Set<Role> roleAssignments = Sets.newHashSet();

	@Override
	public PrismResourceBatchType getId() {
		return id;
	}

	public void setId(PrismResourceBatchType id) {
		this.id = id;
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	@Override
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public Set<Role> getRoleAssignments() {
		return roleAssignments;
	}

	public StateTransitionResourceBatch withId(PrismResourceBatchType id) {
		this.id = id;
		return this;
	}

	public StateTransitionResourceBatch withScope(Scope scope) {
		this.scope = scope;
		return this;
	}

	public void addRoleAssignment(Role role) {
		roleAssignments.add(role);
	}

}
