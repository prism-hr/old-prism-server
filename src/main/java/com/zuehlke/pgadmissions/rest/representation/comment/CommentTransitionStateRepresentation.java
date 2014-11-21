package com.zuehlke.pgadmissions.rest.representation.comment;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class CommentTransitionStateRepresentation {

    private PrismState transitionState;
    
    private Boolean primaryState;

    public final PrismState getTransitionState() {
        return transitionState;
    }

    public final void setTransitionState(PrismState transitionState) {
        this.transitionState = transitionState;
    }

    public final Boolean getPrimaryState() {
        return primaryState;
    }

    public final void setPrimaryState(Boolean primaryState) {
        this.primaryState = primaryState;
    }
    
}
