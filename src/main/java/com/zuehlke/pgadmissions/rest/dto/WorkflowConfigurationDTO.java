package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

public class WorkflowConfigurationDTO {

    @NotNull
    private Enum<?> definitionId;

    public final Enum<?> getDefinitionId() {
        return definitionId;
    }

    public final void setDefinitionId(Enum<?> definitionId) {
        this.definitionId = definitionId;
    }

}
