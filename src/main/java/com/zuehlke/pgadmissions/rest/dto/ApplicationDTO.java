package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDefinition;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;

public class ApplicationDTO implements ResourceCreationDefinition {

    private ResourceDTO parentResource;

    @Override
    public ResourceDTO getParentResource() {
        return parentResource;
    }

    @Override
    public void setParentResource(ResourceDTO parentResource) {
        this.parentResource = parentResource;
    }

}
