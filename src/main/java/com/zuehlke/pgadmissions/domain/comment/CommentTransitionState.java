package com.zuehlke.pgadmissions.domain.comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.workflow.State;

@Entity
@Table(name = "COMMENT_TRANSITION_STATE", uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_id", "transition_state_id" }) })
public class CommentTransitionState {

    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "transition_state_id", nullable = false)
    private State transitionState;

    @Column(name = "primary_state", nullable = false)
    private Boolean primaryState;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Comment getComment() {
        return comment;
    }

    public final void setComment(Comment comment) {
        this.comment = comment;
    }

    public final State getTransitionState() {
        return transitionState;
    }

    public final void setTransitionState(State transitionState) {
        this.transitionState = transitionState;
    }

    public final Boolean getPrimaryState() {
        return primaryState;
    }

    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public CommentTransitionState withTransitionState(State transitionState) {
        this.transitionState = transitionState;
        return this;
    }

    public CommentTransitionState withPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(comment, transitionState);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        CommentTransitionState other = (CommentTransitionState) object;
        return Objects.equal(comment, other.getComment()) && Objects.equal(transitionState, other.getTransitionState());
    }

}
