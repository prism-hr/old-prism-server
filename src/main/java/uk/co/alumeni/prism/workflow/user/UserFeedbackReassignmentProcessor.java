package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserFeedback;
import uk.co.alumeni.prism.services.UserService;

@Component
public class UserFeedbackReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (UserFeedback oldUserFeedback : oldUser.getUserFeedbacks()) {
            userService.mergeUserAssignmentStrict(oldUserFeedback, newUser, userProperty);
        }
    }

}
