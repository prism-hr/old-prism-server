package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.PrismState;

@Entity
@Table(name = "STATE")
public class State {

    @Id
    @Column(name = "id")
    @Enumerated(EnumType.STRING)
    private PrismState id;

    @ManyToOne
    @JoinColumn(name = "parent_state_id", nullable = false)
    private State parentState;

    @Column(name = "is_assessment_state", nullable = false)
    private boolean assessmentState;
    
    @Column(name = "is_fertile_state", nullable = false)
    private boolean fertileState;
    
    public PrismState getId() {
        return id;
    }

    public void setId(PrismState id) {
        this.id = id;
    }

    public State getParentState() {
        return parentState;
    }

    public void setParentState(State parentState) {
        this.parentState = parentState;
    }

    public boolean isAssessmentState() {
        return assessmentState;
    }

    public void setAssessmentState(boolean assessmentState) {
        this.assessmentState = assessmentState;
    }

    public boolean isFertileState() {
        return fertileState;
    }

    public void setFertileState(boolean fertileState) {
        this.fertileState = fertileState;
    }

    public State withId(PrismState id) {
        this.id = id;
        return this;
    }

}
