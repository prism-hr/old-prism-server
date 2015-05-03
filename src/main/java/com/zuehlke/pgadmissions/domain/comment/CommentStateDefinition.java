package com.zuehlke.pgadmissions.domain.comment;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.workflow.State;

public abstract class CommentStateDefinition {

    public abstract Integer getId();

    public abstract void setId(Integer id);

    public abstract Comment getComment();

    public abstract void setComment(Comment comment);

    public abstract State getState();

    public abstract void setState(State state);
    
    public abstract Boolean getPrimaryState();

    public abstract void setPrimaryState(Boolean primaryState);

    @Override
    public int hashCode() {
        return Objects.hashCode(getComment(), getState());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        CommentStateDefinition other = (CommentStateDefinition) object;
        return Objects.equal(getComment(), other.getComment()) && Objects.equal(getState(), other.getState());
    }

}
