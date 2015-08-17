package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.ResourceService;

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
