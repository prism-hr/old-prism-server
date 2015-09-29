package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceRelationInvitationDTO;

public abstract class ApplicationAdvertRelationSectionDTO {

    public abstract ResourceRelationInvitationDTO getResource();

    public abstract void setResource(ResourceRelationInvitationDTO resource);

}
