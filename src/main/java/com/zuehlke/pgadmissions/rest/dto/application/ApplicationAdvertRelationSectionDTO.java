package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceFamilyCreationDTO;

public abstract class ApplicationAdvertRelationSectionDTO {

    public abstract ResourceFamilyCreationDTO getResource();

    public abstract void setResource(ResourceFamilyCreationDTO resource);

}
