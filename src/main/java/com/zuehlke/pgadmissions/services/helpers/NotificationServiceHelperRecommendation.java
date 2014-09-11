package com.zuehlke.pgadmissions.services.helpers;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.NotificationService;

public class NotificationServiceHelperRecommendation extends AbstractServiceHelper {

    @Autowired
    private NotificationService notificationService;
    
    @Override
    public void execute() {
        LocalDate baseline = new LocalDate();
        List<User> users = notificationService.getRecommendationNotifications(baseline);
        for (User user : users) {
            notificationService.sendRecommendationNotification(user, baseline);
        }
    }

}
