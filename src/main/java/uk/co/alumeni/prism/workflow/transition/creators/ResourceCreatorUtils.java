package uk.co.alumeni.prism.workflow.transition.creators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceParentDTO;
import uk.co.alumeni.prism.services.ResourceService;

@Component
public class ResourceCreatorUtils {

    @Inject
    private ResourceService resourceService;

    @SuppressWarnings("unchecked")
    public <T extends ResourceParentDTO, U extends ResourceParent> U getParentResource(T newResource) {
        ResourceDTO parentResourceDTO = newResource.getParentResource();
        return (U) resourceService.getById(parentResourceDTO.getScope().getResourceClass(), parentResourceDTO.getId());
    }

}
