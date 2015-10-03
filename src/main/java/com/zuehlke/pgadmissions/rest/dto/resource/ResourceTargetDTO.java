package com.zuehlke.pgadmissions.rest.dto.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class ResourceTargetDTO {

    @NotNull
    private PrismMotivationContext context;
    
    @Valid
    @NotNull
    private ResourceDTO resource;
    
    @Valid
    private UserDTO user;
    
    public PrismMotivationContext getContext() {
        return context;
    }

    public void setContext(PrismMotivationContext context) {
        this.context = context;
    }

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
    
}
