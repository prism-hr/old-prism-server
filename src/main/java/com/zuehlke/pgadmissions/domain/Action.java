package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;

@Entity
@Table(name = "ACTION")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Action {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAction id;
    
    @Column(name = "action_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionType actionType;
    
    @OneToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
    
    @OneToOne
    @JoinColumn(name = "delegate_action_id")
    private Action delegateAction;

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

}
