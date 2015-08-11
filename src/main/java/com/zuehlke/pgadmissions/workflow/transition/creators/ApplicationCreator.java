package com.zuehlke.pgadmissions.workflow.transition.creators;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ApplicationCreator implements ResourceCreator<ApplicationDTO> {

    @Inject
    private ResourceService resourceService;

    @Override
    public Resource<?> create(User user, ApplicationDTO newResource) {
        ResourceDTO parentResourceDTO = newResource.getParentResource();
        ResourceParent<?> parentResource = (ResourceParent<?>) resourceService.getById(parentResourceDTO.getScope(), parentResourceDTO.getId());
        return new Application().withUser(user).withParentResource(parentResource).withAdvert(parentResource.getAdvert()).withRetain(false);
    }

}
