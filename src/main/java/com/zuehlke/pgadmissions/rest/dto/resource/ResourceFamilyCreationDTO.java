package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismResourceFamilyCreation;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class ResourceFamilyCreationDTO {

    @NotNull
    private PrismResourceFamilyCreation resourceFamilyCreation;
    
    @Valid
    @NotNull
    private List<ResourceCreationDTO> resources;
    
    @Valid
    @NotNull
    private UserDTO user;

    public PrismResourceFamilyCreation getResourceFamilyCreation() {
        return resourceFamilyCreation;
    }

    public void setResourceFamilyCreation(PrismResourceFamilyCreation resourceFamilyCreation) {
        this.resourceFamilyCreation = resourceFamilyCreation;
    }

    public List<ResourceCreationDTO> getResources() {
        return resources;
    }

    public void setResources(List<ResourceCreationDTO> resources) {
        this.resources = resources;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
    
}
