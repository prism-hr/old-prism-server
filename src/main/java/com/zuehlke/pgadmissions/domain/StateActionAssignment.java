package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "STATE_ACTION_ASSIGNMENT", //
    uniqueConstraints = { @UniqueConstraint(columnNames = { "state_action_id", "role_id" }) })
public class StateActionAssignment {

    @Id
    private Integer id;
    
    @Column(name = "state_action_id", nullable = false)
    private StateAction StateAction;
    
    @Column(name = "role_id", nullable = false)
    private Role role;
    
    @Column(name = "precedence")
    private Integer precedence;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StateAction getStateAction() {
        return StateAction;
    }

    public void setStateAction(StateAction stateAction) {
        StateAction = stateAction;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getPrecedence() {
        return precedence;
    }

    public void setPrecedence(Integer precedence) {
        this.precedence = precedence;
    }
    
}
