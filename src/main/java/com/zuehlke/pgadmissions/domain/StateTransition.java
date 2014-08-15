package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
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
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismTransitionEvaluation;

@Entity
@Table(name = "STATE_TRANSITION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_id", "transition_state_id" }) })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StateTransition implements IUniqueEntity {

    @Id
    @GeneratedValue
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
    private PrismTransitionEvaluation stateTransitionEvaluation;

    @OneToMany(mappedBy = "stateTransition")
    private Set<RoleTransition> roleTransitions = Sets.newHashSet();

    @ManyToMany(cascade = CascadeType.ALL)
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

    public PrismTransitionEvaluation getStateTransitionEvaluation() {
        return stateTransitionEvaluation;
    }

    public void setStateTransitionEvaluation(PrismTransitionEvaluation stateTransitionEvaluation) {
        this.stateTransitionEvaluation = stateTransitionEvaluation;
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
    
    public StateTransition withStateTransitionEvaluation(PrismTransitionEvaluation stateTransitionEvaluation) {
        this.stateTransitionEvaluation = stateTransitionEvaluation;
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
