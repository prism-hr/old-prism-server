package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;

@Entity
@Table(name = "ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Action extends WorkflowResource {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAction id;
    
    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionType actionType;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
    @ManyToOne
    @JoinColumn(name = "creation_scope_id")
    private Scope creationScope;
    
    @OneToMany(mappedBy = "action")
    private Set<ActionRedaction> redactions = Sets.newHashSet();

    @Override
    public PrismAction getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = (PrismAction) id;
    }

    public PrismActionType getActionType() {
        return actionType;
    }

    public void setActionType(PrismActionType actionType) {
        this.actionType = actionType;
    }
    
    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getCreationScope() {
        return creationScope;
    }

    public void setCreationScope(Scope creationScope) {
        this.creationScope = creationScope;
    }
    
    public boolean isCreationAction() {
        return creationScope != null;
    }
    
    public boolean isSystemAction() {
        return actionType.isSystemAction();
    }

    public Set<ActionRedaction> getRedactions() {
        return redactions;
    }

    public Action withId(PrismAction id) {
        this.id = id;
        return this;
    }
    
    public Action withActionType(PrismActionType actionType) {
        this.actionType = actionType;
        return this;
    }
    
    public Action withScope(Scope scope) {
        this.scope = scope;
        return this;
    }
    
    public Action withCreationScope(Scope creationScope) {
        this.creationScope = creationScope;
        return this;
    }

}
