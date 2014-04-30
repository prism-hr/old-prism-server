package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Entity
@Table(name = "STATE")
public class State {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private ApplicationFormStatus id;

    @ManyToOne
    @JoinColumn(name = "parent_state_id")
    private State parentState;

    public ApplicationFormStatus getId() {
        return id;
    }

    public void setId(ApplicationFormStatus id) {
        this.id = id;
    }

    public State getParentState() {
        return parentState;
    }

    public void setParentState(State parentState) {
        this.parentState = parentState;
    }

    public State withId(ApplicationFormStatus id) {
        this.id = id;
        return this;
    }

}
