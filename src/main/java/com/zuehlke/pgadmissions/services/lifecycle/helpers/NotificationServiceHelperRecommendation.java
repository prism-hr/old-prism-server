package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class NotificationServiceHelperRecommendation implements PrismServiceHelper {

    @Inject
    private NotificationService notificationService;

    @Inject
    private SystemService systemService;

    @Override
    public void execute() {
        System system = systemService.getSystem();
        LocalDate baseline = new LocalDate();
        LocalDate lastBaseline = system.getLastNotifiedRecommendationSyndicated();

        if (lastBaseline == null || lastBaseline.isBefore(baseline)) {
            LocalDate lastRecommendedBaseline = baseline.minusDays(7);

            List<Integer> users = notificationService.getRecommendationDefinitions(lastRecommendedBaseline);
            for (Integer user : users) {
                notificationService.sendRecommendationNotification(user, baseline, lastRecommendedBaseline);
            }

            systemService.setLastNotifiedRecommendationSyndicated(baseline);
        }
    }

    @Override
    public void shutdown() {
        return;
    }

}
