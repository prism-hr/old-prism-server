package com.zuehlke.pgadmissions.domain.workflow;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;

@Entity
@Table(name = "STATE_GROUP", uniqueConstraints = { @UniqueConstraint(columnNames = { "scope_id", "ordinal" }) })
public class StateGroup extends WorkflowDefinition {

	@Id
    @Enumerated(EnumType.STRING)
	private PrismStateGroup id;

	@Column(name = "ordinal", nullable = false)
	private Integer ordinal;

	@Column(name = "repeatable")
	private Boolean repeatable;

	@ManyToOne
	@JoinColumn(name = "scope_id", nullable = false)
	private Scope scope;

	@Override
	public PrismStateGroup getId() {
		return id;
	}

	public void setId(PrismStateGroup id) {
		this.id = id;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
	}

	public final Boolean getRepeatable() {
		return repeatable;
	}

	public final void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}

	@Override
	public Scope getScope() {
		return scope;
	}

	@Override
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public StateGroup withId(PrismStateGroup id) {
		this.id = id;
		return this;
	}

	public StateGroup withSequenceOrder(Integer ordinal) {
		this.ordinal = ordinal;
		return this;
	}

	public StateGroup withRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
		return this;
	}

	public StateGroup withScope(Scope scope) {
		this.scope = scope;
		return this;
	}

}
