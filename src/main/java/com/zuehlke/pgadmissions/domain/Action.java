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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;

@Entity
@Table(name = "ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Action implements IUniqueResource {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAction id;
    
    @Column(name = "action_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionType actionType;
    
    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
    @OneToOne
    @JoinColumn(name = "delegate_action_id")
    private Action delegateAction;
    
    public Action() {
    }

    public Action(PrismAction id, PrismActionType actionType, Scope scope) {
        this.id = id;
        this.actionType = actionType;
        this.scope = scope;
    }

    public PrismAction getId() {
        return id;
    }

    public void setId(PrismAction id) {
        this.id = id;
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

    public Action getDelegateAction() {
        return delegateAction;
    }

    public void setDelegateAction(Action delegateAction) {
        this.delegateAction = delegateAction;
    }

    public Action withId(PrismAction id) {
        this.id = id;
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
