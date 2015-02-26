package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationServiceHelperWorkflow implements AbstractServiceHelper {

    @Autowired
    private CommentService commentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ScopeService scopeService;

    @Autowired
    private SystemService systemService;

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

    private void sendIndividualRequestReminders(PrismScope scopeId, LocalDate baseline) {
        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        List<Integer> resourceIds = resourceService.getResourcesRequiringIndividualReminders(resourceClass, baseline);
        for (Integer resourceId : resourceIds) {
            notificationService.sendIndividualRequestReminders(resourceClass, resourceId, baseline);
        }
    }

    private void sendSyndicatedRequestNotifications(PrismScope scopeId, LocalDate baseline) {
        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        List<Integer> resourceIds = resourceService.getResourcesRequiringSyndicatedReminders(resourceClass, baseline);
        for (Integer resourceId : resourceIds) {
            notificationService.sendSyndicatedRequestNotifications(resourceClass, resourceId, baseline);
        }
    }

    private void sendSyndicatedUpdateNotifications(PrismScope scopeId, LocalDate baseline) {
        DateTime rangeStart = baseline.minusDays(1).toDateTimeAtStartOfDay();
        DateTime rangeClose = rangeStart.plusDays(1).minusSeconds(1);
        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        List<Integer> resourceIds = resourceService.getResourcesRequiringSyndicatedUpdates(resourceClass, baseline, rangeStart, rangeClose);
        for (Integer resourceId : resourceIds) {
            List<Comment> comments = commentService.getRecentComments(resourceClass, resourceId, rangeStart, rangeClose);
            for (Comment comment : comments) {
                notificationService.sendSyndicatedUpdateNotifications(resourceClass, resourceId, comment, baseline);
            }
        }
    }

}
