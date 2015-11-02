package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismResourceRelationContext;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class ResourceRelationCreationDTO {

    @NotNull
    private PrismResourceRelationContext context;

    @Valid
    @NotNull
    private ResourceRelationDTO resource;

    @Valid
    private UserDTO user;
    
    private String message;

    public PrismResourceRelationContext getContext() {
        return context;
    }

    public void setContext(PrismResourceRelationContext context) {
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
