package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismResourceCreation;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;

public class ResourceFamilyCreationDTO {

    private PrismResourceCreation context;
    
    private List<ResourceCreationDTO> resources;
    
    private UserDTO user;

    public PrismResourceCreation getContext() {
        return context;
    }

    public void setContext(PrismResourceCreation context) {
        this.context = context;
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
