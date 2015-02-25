package com.zuehlke.pgadmissions.domain.resource;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismResourceBatchProcess;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.domain.workflow.WorkflowDefinition;

@Entity
@Table(name = "RESOURCE_BATCH_PROCESS")
public class ResourceBatchProcess extends WorkflowDefinition {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismResourceBatchProcess id;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "RESOURCE_BATCH_PROCESS_SCOPE", joinColumns = { @JoinColumn(name = "resource_batch_process_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "batch_scope_id", nullable = false) })
	private Set<Scope> batchScopes = Sets.newHashSet();

    @Override
	public PrismResourceBatchProcess getId() {
		return id;
	}

	public void setId(PrismResourceBatchProcess id) {
		this.id = id;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	public Set<Scope> getBatchScopes() {
		return batchScopes;
	}
	
    public ResourceBatchProcess withId(PrismResourceBatchProcess id) {
        this.id = id;
        return this;
    }

    public ResourceBatchProcess withScope(Scope scope) {
        this.scope = scope;
        return this;
    }
    
	public void addBatchScope(Scope scope) {
		batchScopes.add(scope);
	}
	
}
