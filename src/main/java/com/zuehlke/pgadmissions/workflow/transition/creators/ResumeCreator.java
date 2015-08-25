package com.zuehlke.pgadmissions.workflow.transition.creators;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.RESUME;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.application.ApplicationDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceDTO;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.ResumeService;

@Component
public class ResumeCreator implements ResourceCreator<ApplicationDTO> {

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ResumeService resumeService;

    @Override
    public Resource create(User user, ApplicationDTO newResource) {
        ResourceDTO parentResourceDTO = newResource.getParentResource();
        ResourceParent parentResource = (ResourceParent) resourceService.getById(parentResourceDTO.getScope(), parentResourceDTO.getId());
        Resume resume = new Resume().withScope(RESUME).withUser(user).withParentResource(parentResource).withOpportunityCategory(newResource.getOpportunityCategory());

        Resume persistentResume = entityService.getDuplicateEntity(resume);
        if (persistentResume != null) {
            resumeService.retireResume(persistentResume);
        }

        return resume;
    }

}
