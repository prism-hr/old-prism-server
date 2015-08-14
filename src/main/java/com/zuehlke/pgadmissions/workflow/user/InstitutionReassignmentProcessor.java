package com.zuehlke.pgadmissions.workflow.user;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.ResourceService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class InstitutionReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private ResourceService resourceService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (Institution oldInstitution : oldUser.getInstitutions()) {
            resourceService.reassignResource(oldInstitution, newUser, userProperty);
        }
    }
}
