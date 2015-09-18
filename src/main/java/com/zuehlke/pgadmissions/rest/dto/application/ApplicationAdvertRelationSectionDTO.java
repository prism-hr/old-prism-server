package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;

public abstract class ApplicationAdvertRelationSectionDTO {

    public abstract ResourceCreationDTO getResource();

    public abstract void setResource(ResourceCreationDTO resource);

}
