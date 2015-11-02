package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class ApplicationQualificationReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (ApplicationQualification oldApplicationQualification : oldUser.getApplicationQualifications()) {
            userService.mergeUserAssignmentStrict(oldApplicationQualification, newUser, userProperty);
        }
    }

}
