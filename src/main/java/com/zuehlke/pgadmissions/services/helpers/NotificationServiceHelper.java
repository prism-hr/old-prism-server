package com.zuehlke.pgadmissions.services.helpers;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class NotificationServiceHelper {

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
    
    public void sendDeferredWorkflowNotifications() {
        LocalDate baseline = new LocalDate();
        List<PrismScope> scopeIds = scopeService.getScopesDescending();
        for (PrismScope scopeId : scopeIds) {
            sendIndividualRequestReminders(scopeId, baseline);
            sendSyndicatedRequestNotifications(scopeId, baseline);
            sendSyndicatedUpdateNotifications(scopeId, baseline);
        }
    }
    
    public void sendRecommendationNotifications() {
        LocalDate baseline = new LocalDate();
        List<User> users = notificationService.getRecommendationNotifications(baseline);
        for (User user : users) {
            notificationService.sendRecommendationNotification(user, baseline);
        }
    }
    
    private void sendIndividualRequestReminders(PrismScope scopeId, LocalDate baseline) {
        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        List<Integer> resourceIds = resourceService.getResourcesRequiringAttention(resourceClass);
        for (Integer resourceId : resourceIds) {
            notificationService.sendIndividualRequestReminders(resourceClass, resourceId, baseline);
        }
    }
    
    private void sendSyndicatedRequestNotifications(PrismScope scopeId, LocalDate baseline) {
        Class<? extends Resource> resourceClass = scopeId.getResourceClass();
        List<Integer> resourceIds = resourceService.getResourcesRequiringAttention(resourceClass);
        for (Integer resourceId : resourceIds) {
            notificationService.sendSyndicatedRequestNotifications(resourceClass, resourceId, baseline);
        }
    }
    
    private void sendSyndicatedUpdateNotifications(PrismScope scopeId, LocalDate baseline) {
        DateTime rangeStart = baseline.minusDays(1).toDateTimeAtStartOfDay();
        DateTime rangeClose = rangeStart.plusDays(1).minusSeconds(1);
        Class<? extends Resource> resourceClass = scopeId.getResourceClass(); 
        List<Integer> resourceIds = resourceService.getRecentlyUpdatedResources(resourceClass, rangeStart, rangeClose);
        for (Integer resourceId : resourceIds) {
            List<Comment> transitionComments = commentService.getTransitionComments(resourceClass, resourceId, rangeStart, rangeClose);
            for (Comment transitionComment : transitionComments) {
                notificationService.sendSyndicatedUpdateNotifications(resourceClass, resourceId, transitionComment, baseline);
            }
        }
    }
    
}
