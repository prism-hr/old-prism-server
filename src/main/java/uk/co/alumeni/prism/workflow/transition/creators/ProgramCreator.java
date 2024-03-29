package uk.co.alumeni.prism.workflow.transition.creators;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;

import javax.inject.Inject;

@Component
public class ProgramCreator implements ResourceCreator<ResourceOpportunityDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResourceCreatorUtils resourceCreatorUtils;

    @Override
    public Resource create(User user, ResourceOpportunityDTO newResource) {
        ResourceParent parentResource = resourceCreatorUtils.getParentResource(newResource);
        Advert advert = advertService.createAdvert(newResource, parentResource, user);
        Program program = new Program().withImportedCode(newResource.getImportedCode()).withUser(user)
                .withParentResource(parentResource).withAdvert(advert).withName(advert.getName());
        advertService.updateDuration(advert, newResource.getDurationMinimum(), newResource.getDurationMaximum());
        resourceService.setResourceAttributes(program, newResource);
        return program;
    }

}
