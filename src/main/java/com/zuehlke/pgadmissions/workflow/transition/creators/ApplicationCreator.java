package com.zuehlke.pgadmissions.workflow.transition.creators;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.services.EntityService;

@Component
public class ApplicationCreator implements ResourceCreator {

    @Inject
    private EntityService entityService;

    @Override
    public Resource create(User user, ResourceDTO newResource) throws Exception {
        ResourceParent parentResource = (ResourceParent) entityService.getById(newResource.getResourceScope().getResourceClass(), newResource.getResourceId());
        return new Application().withUser(user).withParentResource(parentResource).withAdvert(parentResource.getAdvert()).withRetain(false)
                .withCreatedTimestamp(new DateTime());
    }

}
