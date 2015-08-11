package com.zuehlke.pgadmissions.workflow.transition.creators;

import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceParentDivisionDTO;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ResourceCreatorUtils {

    @Inject
    private ResourceService resourceService;

    @SuppressWarnings("unchecked")
    public <T extends ResourceParentDivisionDTO, U extends ResourceParent<?>> U getParentResource(T newResource) {
        ResourceDTO parentResourceDTO = newResource.getParentResource();
        return (U) resourceService.getById(parentResourceDTO.getScope().getResourceClass(), parentResourceDTO.getId());
    }

}
