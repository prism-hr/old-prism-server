package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class NotificationServiceHelperRecommendation extends PrismServiceHelperAbstract {

    @Inject
    private NotificationService notificationService;

    @Inject
    private SystemService systemService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        System system = systemService.getSystem();
        LocalDate baseline = new LocalDate();
        LocalDate lastBaseline = system.getLastNotifiedRecommendationSyndicated();

        if (lastBaseline == null || lastBaseline.isBefore(baseline)) {
            LocalDate lastRecommendedBaseline = baseline.minusDays(7);

            List<Integer> users = notificationService.getRecommendationDefinitions(lastRecommendedBaseline);
            for (Integer user : users) {
                sendRecommendationNotification(user, baseline, lastRecommendedBaseline);
            }

            setLastNotifiedRecommendationSyndicated(baseline);
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void sendRecommendationNotification(Integer user, LocalDate baseline, LocalDate lastRecommendedBaseline) {
        if (!isShuttingDown()) {
            notificationService.sendRecommendationNotification(user, baseline, lastRecommendedBaseline);
        }
    }

    private void setLastNotifiedRecommendationSyndicated(LocalDate baseline) {
        if (!isShuttingDown()) {
            systemService.setLastNotifiedRecommendationSyndicated(baseline);
        }
    }

}
