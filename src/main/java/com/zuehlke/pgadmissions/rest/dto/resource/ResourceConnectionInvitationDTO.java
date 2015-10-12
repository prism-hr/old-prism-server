package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ResourceConnectionInvitationDTO {

    @Valid
    @NotNull
    private ResourceDTO invitingResource;
    
    @Valid
    @NotNull
    private ResourceTargetDTO receivingResource;

    public ResourceDTO getInvitingResource() {
        return invitingResource;
    }

    public void setInvitingResource(ResourceDTO invitingResource) {
        this.invitingResource = invitingResource;
    }

    public ResourceTargetDTO getReceivingResource() {
        return receivingResource;
    }

    public void setReceivingResource(ResourceTargetDTO receivingResource) {
        this.receivingResource = receivingResource;
    }
    
}
