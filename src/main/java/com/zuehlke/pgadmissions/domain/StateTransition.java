package com.zuehlke.pgadmissions.domain;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionEvaluation;

@Entity
@Table(name = "STATE_TRANSITION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_id", "transition_state_id" }) })
public class StateTransition {

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

    @Column(name = "state_transition_evaluation_id")
    @Enumerated(EnumType.STRING)
    private StateTransitionEvaluation evaluation;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "do_post_comment", nullable = false)
    private boolean doPostComment;

    @ManyToMany
    @JoinTable(name = "STATE_TRANSITION_PROPAGATION", joinColumns = { @JoinColumn(name = "state_transition_id", nullable = false) }, //
    inverseJoinColumns = { @JoinColumn(name = "action_id", nullable = false) }, //
    uniqueConstraints = { @UniqueConstraint(columnNames = { "state_transition_id", "action_id" }) })
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

    public StateTransitionEvaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(StateTransitionEvaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isDoPostComment() {
        return doPostComment;
    }

    public void setDoPostComment(boolean doPostComment) {
        this.doPostComment = doPostComment;
    }

    public Set<Action> getPropagatedActions() {
        return propagatedActions;
    }

}
