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

import com.zuehlke.pgadmissions.domain.enums.StateTransitionEvaluation;

@Entity
@Table(name = "STATE_TRANSITION")
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

    @ManyToMany
    @JoinTable(name = "state_transition_propagation", joinColumns = { @JoinColumn(name = "state_transition_id", nullable = false) }, //
    inverseJoinColumns = { @JoinColumn(name = "propagated_state_transition_id", nullable = false) })
    private Set<StateTransition> propagatedStates;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "do_post_comment", nullable = false)
    private boolean doPostComment;

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

}
