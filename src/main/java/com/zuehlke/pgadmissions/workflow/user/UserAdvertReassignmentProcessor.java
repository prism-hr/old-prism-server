package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAdvert;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserAdvertReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (UserAdvert oldUserAdvert : oldUser.getUserAdverts()) {
            userService.mergeUserAssignmentStrict(oldUserAdvert, newUser, userProperty);
        }
    }

}
