package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ResourceConnectionInvitationsDTO {

    @NotNull
    private ResourceDTO resourceDTO;

    @Valid
    private List<ResourceRelationCreationDTO> invitations;

    @Valid
    private List<ResourceConnectionInvitationDTO> connections;

    private String message;

    public ResourceDTO getResourceDTO() {
        return resourceDTO;
    }

    public void setResourceDTO(ResourceDTO resourceDTO) {
        this.resourceDTO = resourceDTO;
    }

    public List<ResourceRelationCreationDTO> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<ResourceRelationCreationDTO> invitations) {
        this.invitations = invitations;
    }

    public List<ResourceConnectionInvitationDTO> getConnections() {
        return connections;
    }

    public void setConnections(List<ResourceConnectionInvitationDTO> connections) {
        this.connections = connections;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
