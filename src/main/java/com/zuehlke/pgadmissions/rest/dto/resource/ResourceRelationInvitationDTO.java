package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismMotivationContext;
import com.zuehlke.pgadmissions.domain.definitions.PrismScopeRelationContext;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class ResourceRelationInvitationDTO {

    @NotNull
    private PrismMotivationContext context;

    @NotNull
    private PrismScopeRelationContext relationContext;

    @Valid
    @NotNull
    private ResourceActivityDTO resource;

    @Valid
    private UserDTO user;

    public PrismMotivationContext getContext() {
        return context;
    }

    public void setContext(PrismMotivationContext context) {
        this.context = context;
    }

    public PrismScopeRelationContext getRelationContext() {
        return relationContext;
    }

    public void setRelationContext(PrismScopeRelationContext relationContext) {
        this.relationContext = relationContext;
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

}
