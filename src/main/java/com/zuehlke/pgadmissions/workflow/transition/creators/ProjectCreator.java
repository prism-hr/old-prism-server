package com.zuehlke.pgadmissions.workflow.transition.creators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ProjectCreator implements ResourceCreator<ResourceOpportunityDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Override
    public Resource create(User user, ResourceOpportunityDTO newResource) throws Exception {
        ResourceDTO parentResourceDTO = newResource.getParentResource();
        ResourceParent parentResource = (ResourceParent) resourceService.getById(parentResourceDTO.getResourceScope(), parentResourceDTO.getResourceId());

        AdvertDTO advertDTO = newResource.getAdvert();
        Advert advert = advertService.createAdvert(parentResource, advertDTO);

        Project project = new Project().withUser(user).withParentResource(parentResource).withAdvert(advert).withTitle(advert.getTitle())
                .withDurationMinimum(newResource.getDurationMinimum()).withDurationMaximum(newResource.getDurationMaximum());

        resourceService.setResourceAttributes(project, newResource);
        return project;
    }

}
