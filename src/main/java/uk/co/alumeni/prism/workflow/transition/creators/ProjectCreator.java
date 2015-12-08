package uk.co.alumeni.prism.workflow.transition.creators;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.advert.AdvertDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;

import javax.inject.Inject;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROJECT;

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
        Advert advert = advertService.createAdvert(newResource, newResource.getName(), user);

        Project project = new Project().withImportedCode(newResource.getImportedCode()).withUser(user).withParentResource(parentResource).withAdvert(advert)
                .withName(advert.getName()).withDurationMinimum(newResource.getDurationMinimum()).withDurationMaximum(newResource.getDurationMaximum());
        advertService.updateOpportunity(project, newResource);

        resourceService.setResourceAttributes(project, newResource);
        return project;
    }

}
