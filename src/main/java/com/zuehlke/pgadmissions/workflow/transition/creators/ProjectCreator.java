package com.zuehlke.pgadmissions.workflow.transition.creators;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceOpportunityDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ProjectCreator implements ResourceCreator<ResourceOpportunityDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResourceCreatorUtils resourceCreatorUtils;

    @Override
    public Resource create(User user, ResourceOpportunityDTO newResource) {
        ResourceParent parentResource = resourceCreatorUtils.getParentResource(newResource);

        if (newResource.getAdvert() == null) {
            newResource.setAdvert(new AdvertDTO());
        }
        AdvertDTO advertDTO = newResource.getAdvert();
        if (advertDTO.getGloballyVisible() == null) {
            advertDTO.setGloballyVisible(PROJECT.isDefaultShared());
        }
        Advert advert = advertService.createAdvert(parentResource, advertDTO, newResource.getName(), user);

        Project project = new Project().withImportedCode(newResource.getImportedCode()).withUser(user).withParentResource(parentResource).withAdvert(advert)
                .withName(advert.getName()).withDurationMinimum(newResource.getDurationMinimum()).withDurationMaximum(newResource.getDurationMaximum());

        resourceService.setResourceAttributes(project, newResource);
        return project;
    }

}
