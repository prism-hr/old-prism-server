package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismScopeRelationContext;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class ResourceRelationInvitationDTO {

    @NotNull
    private PrismScopeRelationContext context;

    @Valid
    @NotNull
    private ResourceActivityDTO resource;

    @Valid
    private UserDTO user;

    private String message;

    public PrismScopeRelationContext getContext() {
        return context;
    }

    public void setContext(PrismScopeRelationContext context) {
        this.context = context;
    }

    public ResourceActivityDTO getResource() {
        return resource;
    }

    public void setResource(ResourceActivityDTO resource) {
        this.resource = resource;
    }

    public List<ResourceCreationDTO> getResources() {
        return resource.getResources();
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
