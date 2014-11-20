package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class CommentTransitionStateDTO {

    @NotNull
    private PrismState transitionState;
    
    @NotNull
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
