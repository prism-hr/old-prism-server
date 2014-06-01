package com.zuehlke.pgadmissions.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "STATE_TRANSITION_PROPAGATION", uniqueConstraints = { @UniqueConstraint(columnNames = { "state_transition_id", "action_id" }) })
public class StateTransitionPropagation {

    @Id
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "state_transition_id", nullable = false)
    private StateTransition stateTransition;
    
    @ManyToOne
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateTransition getStateTransition() {
        return stateTransition;
    }

    public void setStateTransition(StateTransition stateTransition) {
        this.stateTransition = stateTransition;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
    
}
