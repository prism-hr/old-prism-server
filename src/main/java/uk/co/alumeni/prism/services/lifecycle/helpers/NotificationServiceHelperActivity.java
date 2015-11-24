package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.services.NotificationService;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class NotificationServiceHelperActivity extends PrismServiceHelperAbstract {

    @Inject
    private NotificationService notificationService;

    @Inject
    private UserService userService;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private UserMapper userMapper;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        userService.getUsersForActivityRepresentation().forEach(user -> {
            sendUserActivityNotification(user);
        });
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void sendUserActivityNotification(Integer user) {
        if (!isShuttingDown()) {
            UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentation(user);
            AdvertListRepresentation advertListRepresentation = advertMapper.getAdvertExtendedRepresentations(user);
            notificationService.sendUserActivityNotification(user, userActivityRepresentation, advertListRepresentation);
        }
    }

}
