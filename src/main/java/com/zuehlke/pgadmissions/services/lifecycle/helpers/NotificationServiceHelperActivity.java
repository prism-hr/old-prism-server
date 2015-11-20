package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class NotificationServiceHelperActivity extends PrismServiceHelperAbstract {

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserService userService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        userService.getUsersWithActivity().forEach(user -> {
            sendUserActivityNotification(user);
        });
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void sendUserActivityNotification(Integer user) {
        if (!isShuttingDown()) {
            notificationService.sendUserActivityNotification(user);
        }
    }

}
