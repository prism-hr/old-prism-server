package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;

public class ResourceConnectionInvitationsDTO {

    @Valid
    private List<ResourceRelationInvitationDTO> invitations;

    @Valid
    private List<ResourceConnectionInvitationDTO> connections;

    private String message;

    public List<ResourceRelationInvitationDTO> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<ResourceRelationInvitationDTO> invitations) {
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
