package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ApplicationReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private ResourceService resourceService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (Application oldResume : oldUser.getResumes()) {
            resourceService.reassignResource(oldResume, newUser, userProperty);
        }

        for (Application oldApplication : oldUser.getApplications()) {
            resourceService.reassignResource(oldApplication, newUser, userProperty);
        }
    }

}
