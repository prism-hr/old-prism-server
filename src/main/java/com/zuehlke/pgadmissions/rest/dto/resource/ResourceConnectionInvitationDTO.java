package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ResourceConnectionInvitationDTO {

    @Valid
    @NotNull
    private ResourceCreationDTO invitingResource;

    @Valid
    @NotNull
    private ResourceRelationCreationDTO receivingResource;

    public ResourceCreationDTO getInvitingResource() {
        return invitingResource;
    }

    public void setInvitingResource(ResourceCreationDTO invitingResource) {
        this.invitingResource = invitingResource;
    }

    public ResourceRelationCreationDTO getReceivingResource() {
        return receivingResource;
    }

    public void setReceivingResource(ResourceRelationCreationDTO receivingResource) {
        this.receivingResource = receivingResource;
    }

}
