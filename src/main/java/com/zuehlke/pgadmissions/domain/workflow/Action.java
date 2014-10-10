package com.zuehlke.pgadmissions.domain.workflow;

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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;

@Entity
@Table(name = "ACTION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Action extends WorkflowDefinition {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismAction id;

    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionType actionType;

    @Column(name = "action_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismActionCategory actionCategory;
    
    @Column(name = "rating_action", nullable = false)
    private Boolean ratingAction;
    
    @Column(name = "transition_action", nullable = false)
    private Boolean transitionAction;

    @ManyToOne
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @ManyToOne
    @JoinColumn(name = "fallback_action_id")
    private Action fallbackAction;
    
    @ManyToOne
    @JoinColumn(name = "creation_scope_id")
    private Scope creationScope;

    @OneToMany(mappedBy = "action")
    private Set<ActionRedaction> redactions = Sets.newHashSet();
    
    @OneToMany(mappedBy = "action")
    private Set<StateAction> stateActions = Sets.newHashSet();

    @Override
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

    public PrismActionCategory getActionCategory() {
        return actionCategory;
    }

    public void setActionCategory(PrismActionCategory actionCategory) {
        this.actionCategory = actionCategory;
    }

    public final Boolean isRatingAction() {
        return ratingAction;
    }

    public final void setRatingAction(Boolean ratingAction) {
        this.ratingAction = ratingAction;
    }

    public final Boolean isTransitionAction() {
        return transitionAction;
    }

    public final void setTransitionAction(Boolean transitionAction) {
        this.transitionAction = transitionAction;
    }

    public final Action getFallbackAction() {
        return fallbackAction;
    }

    public final void setFallbackAction(Action fallbackAction) {
        this.fallbackAction = fallbackAction;
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

    public Set<ActionRedaction> getRedactions() {
        return redactions;
    }

    public final Set<StateAction> getStateActions() {
        return stateActions;
    }

    public final void setStateActions(Set<StateAction> stateActions) {
        this.stateActions = stateActions;
    }

    public Action withId(PrismAction id) {
        this.id = id;
        return this;
    }

    public Action withActionType(PrismActionType actionType) {
        this.actionType = actionType;
        return this;
    }
    
    public Action withActionCategory(PrismActionCategory actionCategory) {
        this.actionCategory = actionCategory;
        return this;
    }
    
    public Action withRatingAction(Boolean ratingAction) {
        this.ratingAction = ratingAction;
        return this;
    }
    
    public Action withTransitionAction(Boolean transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }
    
    public Action withFallbackAction(Action fallbackAction) {
        this.fallbackAction = fallbackAction;
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
