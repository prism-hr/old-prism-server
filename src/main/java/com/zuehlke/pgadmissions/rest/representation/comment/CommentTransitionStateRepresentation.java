package com.zuehlke.pgadmissions.rest.representation.comment;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class CommentTransitionStateRepresentation {

    private PrismState state;

    private Boolean primaryState;

    public final PrismState getState() {
        return state;
    }

    public final void setState(PrismState state) {
        this.state = state;
    }

    public final Boolean getPrimaryState() {
        return primaryState;
    }

    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }

}
