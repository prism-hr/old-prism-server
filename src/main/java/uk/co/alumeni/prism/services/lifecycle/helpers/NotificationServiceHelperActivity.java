package uk.co.alumeni.prism.services.lifecycle.helpers;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.mapping.AdvertMapper;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.services.NotificationService;
import uk.co.alumeni.prism.services.UserService;

import javax.inject.Inject;
import java.util.List;
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
        userService.getUsersForActivityNotification().forEach(this::sendUserActivityNotification);
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void sendUserActivityNotification(Integer user) {
        if (!isShuttingDown()) {
            UserActivityRepresentation userActivityRepresentation = userMapper.getUserActivityRepresentation(user);
            if (userActivityRepresentation != null) {
                List<UserActivityRepresentation.ResourceActivityRepresentation> resourceActivityRepresentations =
                        userMapper.filterResourceActivitiesForNotification(userActivityRepresentation.getResourceActivities());
                if (CollectionUtils.isNotEmpty(resourceActivityRepresentations)) {
                    AdvertListRepresentation advertListRepresentation = advertMapper.getAdvertExtendedRepresentations(user);
                    notificationService.sendUserActivityNotification(user, userActivityRepresentation, advertListRepresentation);
                }
            }
        }
    }

}
