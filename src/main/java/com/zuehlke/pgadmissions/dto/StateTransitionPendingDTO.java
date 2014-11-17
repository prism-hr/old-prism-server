package com.zuehlke.pgadmissions.dto;

public class StateTransitionPendingDTO {

    private Integer id;
    
    private Integer resourceId;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Integer getResourceId() {
        return resourceId;
    }

    public final void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }
    
}
