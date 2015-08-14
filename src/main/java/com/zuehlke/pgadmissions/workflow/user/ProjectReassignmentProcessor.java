package com.zuehlke.pgadmissions.workflow.user;

import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ProjectReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private ResourceService resourceService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (Project oldProject : oldUser.getProjects()) {
            resourceService.reassignResource(oldProject, newUser, userProperty);
        }
    }

}
