package com.zuehlke.pgadmissions.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class UserFeedbackReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) throws Exception {
        for (UserFeedback oldUserFeedback : oldUser.getUserFeedbacks()) {
            userService.mergeUserAssignmentStrict(oldUserFeedback, newUser, userProperty);
        }
    }

}
