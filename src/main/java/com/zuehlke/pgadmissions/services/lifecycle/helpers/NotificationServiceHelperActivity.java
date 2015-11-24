package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.mapping.AdvertMapper;
import com.zuehlke.pgadmissions.mapping.UserMapper;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertListRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.UserService;

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
