package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.NotificationService;

@Component
public class NotificationServiceHelperRecommendation implements AbstractServiceHelper {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void execute() {
        notificationService.sendRecommendationNotifications();
    }

}
