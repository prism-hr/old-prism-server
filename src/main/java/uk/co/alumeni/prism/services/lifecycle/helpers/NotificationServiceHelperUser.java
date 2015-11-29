package uk.co.alumeni.prism.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.services.NotificationService;

@Service
public class NotificationServiceHelperUser extends PrismServiceHelperAbstract {

    @Inject
    private NotificationService notificationService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        processEmailBounces();
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void processEmailBounces() {
        if (!isShuttingDown()) {
            notificationService.resetUserNotifications();
        }
    }

}
