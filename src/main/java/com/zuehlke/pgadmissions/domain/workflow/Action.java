package com.zuehlke.pgadmissions.domain.workflow;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "action")
public class Action extends WorkflowDefinition {

    @Id
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

    @Column(name = "transition_action")
    private Boolean transitionAction;

    @Column(name = "declinable_action", nullable = false)
    private Boolean declinableAction;

    @Column(name = "visible_action", nullable = false)
    private Boolean visibleAction;

    @OneToOne
    @JoinColumn(name = "action_custom_question_definition_id")
    private ActionCustomQuestionDefinition actionCustomQuestionDefinition;

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

    public final Boolean getRatingAction() {
        return ratingAction;
    }

    public final void setRatingAction(Boolean ratingAction) {
        this.ratingAction = ratingAction;
    }

    public final Boolean getTransitionAction() {
        return transitionAction;
    }

    public final void setTransitionAction(Boolean transitionAction) {
        this.transitionAction = transitionAction;
    }

    public final Boolean getDeclinableAction() {
        return declinableAction;
    }

    public final void setDeclinableAction(Boolean declinableAction) {
        this.declinableAction = declinableAction;
    }

    public final Boolean getVisibleAction() {
        return visibleAction;
    }

    public final void setVisibleAction(Boolean visibleAction) {
        this.visibleAction = visibleAction;
    }

    public final ActionCustomQuestionDefinition getActionCustomQuestionDefinition() {
        return actionCustomQuestionDefinition;
    }

    public final void setActionCustomQuestionDefinition(ActionCustomQuestionDefinition actionCustomQuestionDefinition) {
        this.actionCustomQuestionDefinition = actionCustomQuestionDefinition;
    }

    public final Action getFallbackAction() {
        return fallbackAction;
    }

    public final void setFallbackAction(Action fallbackAction) {
        this.fallbackAction = fallbackAction;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
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

    public final void setRedactions(Set<ActionRedaction> redactions) {
        this.redactions = redactions;
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

    public Action withDeclinableAction(Boolean declinableAction) {
        this.declinableAction = declinableAction;
        return this;
    }

    public Action withVisibleAction(Boolean visibleAction) {
        this.visibleAction = visibleAction;
        return this;
    }

    public Action withActionCustomQuestionDefinition(ActionCustomQuestionDefinition actionCustomQuestionDefinition) {
        this.actionCustomQuestionDefinition = actionCustomQuestionDefinition;
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

    public boolean isCustomizableAction() {
        return actionCustomQuestionDefinition != null;
    }

}
