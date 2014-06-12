package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Entity
@Table(name = "STATE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class State implements IUniqueResource {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismState id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_state_id")
    private State parentState;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
    @OneToMany(mappedBy = "state")
    private Set<StateAction> stateActions;
    
    @OneToMany(mappedBy = "state")
    private Set<Application> applications;
    
    public State() {
    }
    
    public State(PrismState id, Scope scope) {
        this.id = id;
        this.scope = scope;
    }

    public PrismState getId() {
        return id;
    }

    public void setId(PrismState id) {
        this.id = id;
    }

    public State getParentState() {
        return parentState;
    }

    public void setParentState(State parentState) {
        this.parentState = parentState;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Set<StateAction> getStateActions() {
        return stateActions;
    }
    
    public State withId(PrismState id) {
        this.id = id;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("id", id);
        properties.put("parentState.id", parentState.getId());
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
