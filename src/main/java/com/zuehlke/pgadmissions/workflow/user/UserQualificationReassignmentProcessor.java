package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserQualification;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserQualificationReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (UserQualification oldUserQualification : oldUser.getUserQualifications()) {
            userService.mergeUserAssignmentStrict(oldUserQualification, newUser, userProperty);
        }
    }

}
