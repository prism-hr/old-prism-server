package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

public class WorkflowConfigurationDTO {

    @NotNull
    private Enum<?> id;

    public final Enum<?> getId() {
        return id;
    }

    public final void setId(Enum<?> id) {
        this.id = id;
    }
    
}
