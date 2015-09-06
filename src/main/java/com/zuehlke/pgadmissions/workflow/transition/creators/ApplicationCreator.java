package com.zuehlke.pgadmissions.workflow.transition.creators;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.services.ResourceService;

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
            opportunityCategory = opportunityCategory == null ? PrismOpportunityCategory.valueOf(((ResourceOpportunity) parentResource).getOpportunityCategories())
                    : opportunityCategory;
        } else {
            opportunityCategory = newResource.getOpportunityCategory();
        }

        return new Application().withScope(APPLICATION).withUser(user).withParentResource(parentResource).withAdvert(parentResource.getAdvert())
                .withOpportunityCategories(opportunityCategory.name());
    }

}
