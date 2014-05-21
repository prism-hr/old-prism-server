package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.enums.StateTransitionType;

@Entity
@Table(name = "STATE_TRANSITION")
public class StateTransition {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "state_action_id", nullable = false)
    private StateAction stateAction;

    @Column(name = "state_transition_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private StateTransitionType type;

    @ManyToOne
    @JoinColumn(name = "transition_state_id", nullable = false)
    private State transitionState;

    @ManyToOne
    @JoinColumn(name = "transition_action_id")
    private Action transitionAction;

    @Column(name = "processing_order")
    private Integer processingOrder;

    @Column(name = "state_transition_evaluation_id")
    @Enumerated(EnumType.STRING)
    private StateTransitionEvaluation evaluation;

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

    public StateTransitionType getType() {
        return type;
    }

    public void setType(StateTransitionType type) {
        this.type = type;
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

    public Integer getProcessingOrder() {
        return processingOrder;
    }

    public void setProcessingOrder(Integer processingOrder) {
        this.processingOrder = processingOrder;
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
