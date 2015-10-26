package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationCreationDTO;

public abstract class ApplicationAdvertRelationSectionDTO {

    public abstract ResourceRelationCreationDTO getResource();

    public abstract void setResource(ResourceRelationCreationDTO resource);

}
