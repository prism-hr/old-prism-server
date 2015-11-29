package uk.co.alumeni.prism.workflow.transition.creators;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PROGRAM;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.advert.AdvertDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceOpportunityDTO;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.ResourceService;

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

        if(newResource.getAdvert() == null){
            newResource.setAdvert(new AdvertDTO());
        }
        AdvertDTO advertDTO = newResource.getAdvert();
        if(advertDTO.getGloballyVisible() == null) {
            advertDTO.setGloballyVisible(PROGRAM.isDefaultShared());
        }
        Advert advert = advertService.createAdvert(parentResource, advertDTO, newResource.getName(), user);

        Program program = new Program().withImportedCode(newResource.getImportedCode()).withUser(user).withParentResource(parentResource).withAdvert(advert)
                .withName(advert.getName()).withDurationMinimum(newResource.getDurationMinimum()).withDurationMaximum(newResource.getDurationMaximum());

        resourceService.setResourceAttributes(program, newResource);
        return program;
    }

}