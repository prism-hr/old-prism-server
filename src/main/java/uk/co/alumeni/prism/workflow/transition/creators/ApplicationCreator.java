package uk.co.alumeni.prism.workflow.transition.creators;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.ResourceOpportunity;
import uk.co.alumeni.prism.domain.resource.ResourceParent;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.application.ApplicationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.services.ResourceService;

@Component
public class ApplicationCreator implements ResourceCreator<ApplicationDTO> {

    @Inject
    private ResourceService resourceService;

    @Override
    public Resource create(User user, ApplicationDTO newResource) {
        ResourceDTO parentResourceDTO = newResource.getParentResource();
        ResourceParent parentResource = (ResourceParent) resourceService.getById(parentResourceDTO.getScope(), parentResourceDTO.getId());

        PrismOpportunityCategory opportunityCategory = newResource.getOpportunityCategory();
        if (ResourceOpportunity.class.isAssignableFrom(parentResource.getClass())) {
            opportunityCategory = opportunityCategory == null ? PrismOpportunityCategory.valueOf(parentResource.getOpportunityCategories())
                    : opportunityCategory;
        } else {
            opportunityCategory = newResource.getOpportunityCategory();
        }

        return new Application().withUser(user).withParentResource(parentResource).withAdvert(parentResource.getAdvert())
                .withOpportunityCategories(opportunityCategory.name()).withOnCourse(false);
    }

}
