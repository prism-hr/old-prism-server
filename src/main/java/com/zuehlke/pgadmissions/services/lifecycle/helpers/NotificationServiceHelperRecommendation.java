package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class NotificationServiceHelperRecommendation implements AbstractServiceHelper {

    @Inject
    private NotificationService notificationService;
    
    @Inject
    private SystemService systemService;
    
    @Inject
    private UserService userService;

    @Override
    public void execute() {
        System system = systemService.getSystem();
        LocalDate baseline = new LocalDate();
        LocalDate lastBaseline = system.getLastNotifiedRecommendationSyndicated();
        
        if (lastBaseline == null || lastBaseline.isBefore(baseline)) {
            LocalDate lastRecommendedBaseline = baseline.minusDays(7);
            
            List<Integer> users = userService.getUsersDueRecommendationNotification(lastRecommendedBaseline);
            for (Integer user : users) {
                notificationService.sendRecommendationNotifications(user, baseline, lastRecommendedBaseline);
            }

            systemService.setLastNotifiedRecommendationSyndicated(baseline);
        }
    }

}
