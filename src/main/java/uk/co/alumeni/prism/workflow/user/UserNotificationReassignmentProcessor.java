package uk.co.alumeni.prism.workflow.user;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserNotification;
import uk.co.alumeni.prism.services.UserService;

@Component
public class UserNotificationReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (UserNotification oldUserNotification : oldUser.getUserNotifications()) {
            userService.mergeUserAssignmentStrict(oldUserNotification, newUser, userProperty);
        }
    }

}
