package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismScopeRelationContext;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class ResourceRelationCreationDTO {

    @NotNull
    private PrismScopeRelationContext context;

    @Valid
    @NotNull
    private ResourceRelationDTO resource;

    @Valid
    private UserDTO user;

    public PrismScopeRelationContext getContext() {
        return context;
    }

    public void setContext(PrismScopeRelationContext context) {
        this.context = context;
    }

    public ResourceRelationDTO getResource() {
        return resource;
    }

    public void setResource(ResourceRelationDTO resource) {
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

}
