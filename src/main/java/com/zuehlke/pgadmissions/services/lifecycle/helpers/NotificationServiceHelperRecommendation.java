package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.NotificationService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationServiceHelperRecommendation implements AbstractServiceHelper {

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
