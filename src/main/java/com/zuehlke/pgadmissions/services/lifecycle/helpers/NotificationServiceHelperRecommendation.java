package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import static com.zuehlke.pgadmissions.utils.PrismConstants.RECOMMENDATION_INTERVAL;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class NotificationServiceHelperRecommendation implements AbstractServiceHelper {
    
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
            LocalDate lastRecommendedBaseline = baseline.minusDays(RECOMMENDATION_INTERVAL);

            List<Integer> users = notificationService.getRecommendationDefinitions(lastRecommendedBaseline);
            for (Integer user : users) {
                notificationService.sendRecommendationNotification(user, baseline, lastRecommendedBaseline);
            }

            systemService.setLastNotifiedRecommendationSyndicated(baseline);
        }
    }

}
