package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class SystemReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        resourceService.reassignResource(systemService.getSystem(), newUser, userProperty);
    }

}
