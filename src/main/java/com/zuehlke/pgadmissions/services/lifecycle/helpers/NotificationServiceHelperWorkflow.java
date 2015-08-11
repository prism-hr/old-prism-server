package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.ScopeService;

@Component
public class NotificationServiceHelperWorkflow extends PrismServiceHelperAbstract {

    @Inject
    private CommentService commentService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ScopeService scopeService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        LocalDate baseline = new LocalDate();
        List<PrismScope> scopeIds = scopeService.getScopesDescending();
        for (PrismScope scopeId : scopeIds) {
            sendIndividualRequestReminders(scopeId, baseline);
            sendSyndicatedRequestNotifications(scopeId, baseline);
            sendSyndicatedUpdateNotifications(scopeId, baseline);
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void sendIndividualRequestReminders(PrismScope resourceScope, LocalDate baseline) {
        List<Integer> resourceIds = resourceService.getResourcesRequiringIndividualReminders(resourceScope, baseline);
        for (Integer resourceId : resourceIds) {
            sendIndividualRequestReminders(resourceScope, resourceId, baseline);
        }
    }

    private void sendIndividualRequestReminders(PrismScope resourceScope, Integer resourceId, LocalDate baseline) {
        if (!isShuttingDown()) {
            notificationService.sendIndividualRequestReminders(resourceScope, resourceId, baseline);
        }
    }

    private void sendSyndicatedRequestNotifications(PrismScope resourceScope, LocalDate baseline) {
        List<Integer> resourceIds = resourceService.getResourcesRequiringSyndicatedReminders(resourceScope, baseline);
        for (Integer resourceId : resourceIds) {
            sendSyndicatedRequestNotifications(resourceScope, resourceId, baseline);
        }
    }

    private void sendSyndicatedRequestNotifications(PrismScope resourceScope, Integer resourceId, LocalDate baseline) {
        if (!isShuttingDown()) {
            notificationService.sendSyndicatedRequestNotifications(resourceScope, resourceId, baseline);
        }
    }

    private void sendSyndicatedUpdateNotifications(PrismScope resourceScope, LocalDate baseline) {
        DateTime rangeStart = baseline.minusDays(1).toDateTimeAtStartOfDay();
        DateTime rangeClose = rangeStart.plusDays(1).minusSeconds(1);
        List<Integer> resourceIds = resourceService.getResourcesRequiringSyndicatedUpdates(resourceScope, baseline, rangeStart, rangeClose);
        for (Integer resourceId : resourceIds) {
            List<Comment> comments = commentService.getRecentComments(resourceScope, resourceId, rangeStart, rangeClose);
            for (Comment comment : comments) {
                sendSyndicatedUpdateNotifications(resourceScope, resourceId, comment, baseline);
            }
        }
    }

    private void sendSyndicatedUpdateNotifications(PrismScope resourceScope, Integer resourceId, Comment comment, LocalDate baseline) {
        if (!isShuttingDown()) {
            notificationService.sendSyndicatedUpdateNotifications(resourceScope, resourceId, comment, baseline);
        }
    }

}
