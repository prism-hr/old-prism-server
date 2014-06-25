package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;

@Entity
@Table(name = "STATE_TRANSITION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_id", "transition_state_id" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class StateTransition implements IUniqueResource {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_action_id", nullable = false)
    private StateAction stateAction;

    @ManyToOne
    @JoinColumn(name = "transition_state_id", nullable = false)
    private State transitionState;

    @ManyToOne
    @JoinColumn(name = "transition_action_id", nullable = false)
    private Action transitionAction;

    @Column(name = "state_transition_evaluation")
    @Enumerated(EnumType.STRING)
    private PrismStateTransitionEvaluation stateTransitionEvaluation;

    @Column(name = "do_post_comment", nullable = false)
    private boolean doPostComment;
    
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    
    @OneToMany(mappedBy = "stateTransition")
    private Set<RoleTransition> roleTransitions = Sets.newHashSet();

    @ManyToMany
    @JoinTable(name = "STATE_TRANSITION_PROPAGATION", joinColumns = { @JoinColumn(name = "state_transition_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "propagated_action_id", nullable = false) })
    private Set<Action> propagatedActions = Sets.newHashSet();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateAction getStateAction() {
        return stateAction;
    }

    public void setStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
    }

    public State getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(State transitionState) {
        this.transitionState = transitionState;
    }

    public Action getTransitionAction() {
        return transitionAction;
    }

    public void setTransitionAction(Action transitionAction) {
        this.transitionAction = transitionAction;
    }

    public PrismStateTransitionEvaluation getStateTransitionEvaluation() {
        return stateTransitionEvaluation;
    }

    public void setStateTransitionEvaluation(PrismStateTransitionEvaluation stateTransitionEvaluation) {
        this.stateTransitionEvaluation = stateTransitionEvaluation;
    }
    
    public boolean isDoPostComment() {
        return doPostComment;
    }

    public void setDoPostComment(boolean doPostComment) {
        this.doPostComment = doPostComment;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<RoleTransition> getRoleTransitions() {
        return roleTransitions;
    }
    
    public Set<Action> getPropagatedActions() {
        return propagatedActions;
    }
    
    public StateTransition withStateAction(StateAction stateAction) {
        this.stateAction = stateAction;
        return this;
    }
    
    public StateTransition withTransitionState(State transitionState) {
        this.transitionState = transitionState;
        return this;
    }

    public StateTransition withTransitionAction(Action transitionAction) {
        this.transitionAction = transitionAction;
        return this;
    }
    
    public StateTransition withStateTransitionEvaluation(PrismStateTransitionEvaluation stateTransitionEvaluation) {
        this.stateTransitionEvaluation = stateTransitionEvaluation;
        return this;
    }
    
    public StateTransition withDoPostComment(boolean doPostComment) {
        this.doPostComment = doPostComment;
        return this;
    }
    
    public StateTransition withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("stateAction", stateAction);
        properties.put("transitionState", transitionState);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
