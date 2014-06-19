package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;

@Entity
@Table(name = "STATE_TRANSITION_EVALUATION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class StateTransitionEvaluation {
    
    @Id
    @Enumerated(EnumType.STRING)
    private PrismStateTransitionEvaluation id;
    
    @Column(name = "method_name", nullable = false)
    private String methodName;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    public PrismStateTransitionEvaluation getId() {
        return id;
    }

    public void setId(PrismStateTransitionEvaluation id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
    
    public StateTransitionEvaluation withId(PrismStateTransitionEvaluation id) {
        this.id = id;
        return this;
    }
    
    public StateTransitionEvaluation withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }
    
    public StateTransitionEvaluation withScope(Scope scope) {
        this.scope = scope;
        return this;
    }

}
