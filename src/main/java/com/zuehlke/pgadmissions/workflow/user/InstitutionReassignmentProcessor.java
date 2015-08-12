package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class InstitutionReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private ResourceService resourceService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) throws Exception {
        for (Institution oldInstitution : oldUser.getInstitutions()) {
            resourceService.reassignResource(oldInstitution, newUser, userProperty);
        }
    }
}
