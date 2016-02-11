package uk.co.alumeni.prism.rest.dto.application;

import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;

public abstract class ApplicationAdvertRelationSectionDTO {

    public abstract ResourceRelationCreationDTO getResource();

    public abstract void setResource(ResourceRelationCreationDTO resource);

}
