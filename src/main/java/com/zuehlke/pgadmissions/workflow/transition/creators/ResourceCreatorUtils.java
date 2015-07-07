package com.zuehlke.pgadmissions.workflow.transition.creators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ResourceCreatorUtils {

    @Inject
    private ResourceService resourceService;

    @SuppressWarnings("unchecked")
    public <T extends ResourceParentDivisionDTO, U extends ResourceParent> U getParentResource(User user, T newResource) throws Exception {
        U parentResource;
        ResourceDTO parentResourceDTO = newResource.getParentResource();
        if (parentResourceDTO == null) {
            parentResource = resourceService.createParentResource(user, newResource.getNewParentResource());
        } else {
            parentResource = (U) resourceService.getById(parentResourceDTO.getResourceScope(), parentResourceDTO.getResourceId());
        }
        return parentResource;
    }

}
