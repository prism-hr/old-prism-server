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
    private ResourceRelationDTO resource;

    @Valid
    private UserDTO user;

    private String message;

    public PrismMotivationContext getContext() {
        return context;
    }

    public void setContext(PrismMotivationContext context) {
        this.context = context;
    }

    public ResourceRelationDTO getResource() {
        return resource;
    }

    public void setResource(ResourceRelationDTO resource) {
        this.resource = resource;
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
