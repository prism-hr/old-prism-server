package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;

@Entity
@Table(name = "STATE_TRANSITION_EVALUATION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class StateTransitionEvaluation implements IUniqueResource {
    
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
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("id", id);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
