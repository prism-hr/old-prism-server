package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

public abstract class WorkflowConfigurationGroupDTO {

    public abstract List<? extends WorkflowConfigurationDTO> getValues();

}
