package uk.co.alumeni.prism.workflow.transition.creators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.Project;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;

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

        Advert advert = advertService.createAdvert(newResource, user);
        advertService.createAdvertLocation(advert, parentResource.getAdvert());

        Project project = new Project().withImportedCode(newResource.getImportedCode()).withUser(user).withParentResource(parentResource).withAdvert(advert)
                .withName(advert.getName()).withDurationMinimum(newResource.getDurationMinimum()).withDurationMaximum(newResource.getDurationMaximum());

        advertService.updateFinancialDetail(advert, newResource.getFinancialDetail());
        resourceService.setResourceAttributes(project, newResource);
        return project;
    }

}
