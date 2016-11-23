package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.services.NotificationService;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class NotificationServiceHelperReminder extends PrismServiceHelperAbstract {

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserService userService;

    @Inject
    private UserMapper userMapper;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        userService.getUsersForReminderNotification().forEach(this::sendUserReminderNotification);
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void sendUserReminderNotification(Integer user) {
        if (!isShuttingDown()) {
            UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentation(user);
            if (userActivityRepresentation != null) {
                notificationService.sendUserReminderNotification(user, userActivityRepresentation);
            }
        }
    }

}
