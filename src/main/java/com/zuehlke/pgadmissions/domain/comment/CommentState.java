package com.zuehlke.pgadmissions.domain.comment;

import com.zuehlke.pgadmissions.domain.workflow.State;

import javax.persistence.*;

@Entity
@Table(name = "comment_state", uniqueConstraints = { @UniqueConstraint(columnNames = { "comment_id", "state_id" }) })
public class CommentState extends CommentStateDefinition {

    @Id
    @GeneratedValue
    Integer id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false, insertable = false, updatable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "primary_state", nullable = false)
    private Boolean primaryState;

    @Override
    public final Integer getId() {
        return id;
    }

    @Override
    public final void setId(Integer id) {
        this.id = id;
    }

    @Override
    public final Comment getComment() {
        return comment;
    }

    @Override
    public final void setComment(Comment comment) {
        this.comment = comment;
    }

    @Override
    public final State getState() {
        return state;
    }

    @Override
    public final void setState(State state) {
        this.state = state;
    }

    @Override
    public final Boolean getPrimaryState() {
        return primaryState;
    }

    @Override
    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

    public CommentState withState(State state) {
        this.state = state;
        return this;
    }

    public CommentState withPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
        return this;
    }

}
