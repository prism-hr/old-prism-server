package uk.co.alumeni.prism.workflow.user;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.message.MessageNotification;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;

@Component
public class MessageNotificationReassignmentProcessor implements PrismUserReassignmentProcessor {

    @Inject
    private UserService userService;

    @Override
    public void reassign(User oldUser, User newUser, String userProperty) {
        for (MessageNotification oldMessageNotification : oldUser.getMessageNotifications()) {
            userService.mergeUserAssignmentStrict(oldMessageNotification, newUser, userProperty);
        }
    }

}
