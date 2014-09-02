package com.zuehlke.pgadmissions.services.helpers;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.User;
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
        for (Scope scope : scopeService.getScopesAscending()) {
            sendIndividualRequestReminders(scope, baseline);
            sendSyndicatedRequestNotifications(scope, baseline);
            sendSyndicatedUpdateNotifications(scope, baseline);
        }
    }
    
    public void sendRecommendationNotifications() {
        LocalDate baseline = new LocalDate();
        List<User> users = notificationService.getRecommendationNotifications(baseline);
        for (User user : users) {
            notificationService.sendRecommendationNotification(user, baseline);
        }
    }
    
    private void sendIndividualRequestReminders(Scope scope, LocalDate baseline) {
        List<Resource> resources = resourceService.getResourcesRequiringAttention(scope.getId().getResourceClass());
        for (Resource resource : resources) {
            notificationService.sendIndividualRequestReminders(resource, baseline);
        }
    }
    
    private void sendSyndicatedRequestNotifications(Scope scope, LocalDate baseline) {
        List<Resource> resources = resourceService.getResourcesRequiringAttention(scope.getId().getResourceClass());
        for (Resource resource : resources) {
            notificationService.sendSyndicatedRequestNotifications(resource, baseline);
        }
    }
    
    private void sendSyndicatedUpdateNotifications(Scope scope, LocalDate baseline) {
        DateTime rangeStart = baseline.minusDays(1).toDateTimeAtStartOfDay();
        DateTime rangeClose = rangeStart.plusDays(1).minusSeconds(1);
        List<Resource> resources = resourceService.getRecentlyUpdatedResources(scope.getId().getResourceClass(), rangeStart, rangeClose);
        for (Resource resource : resources) {
            List<Comment> transitionComments = commentService.getTransitionComments(resource, rangeStart, rangeClose);
            for (Comment transitionComment : transitionComments) {
                notificationService.sendSyndicatedUpdateNotifications(resource, transitionComment, baseline);
            }
        }
    }
    
}
