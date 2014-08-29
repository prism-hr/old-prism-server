package com.zuehlke.pgadmissions.lifecycle;

import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.ScopeService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class NotificationHelper {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ScopeService scopeService;
    
    @Autowired
    private SystemService systemService;
    
    public void sendDeferredWorkflowNotifications() {        
        User invoker = systemService.getSystem().getUser();
        LocalDate baseline = new LocalDate();

        for (Scope scope : scopeService.getScopesAscending()) {
            sendRequestReminders(scope, invoker, baseline);
            sendSyndicatedWorkflowNotifications(scope, invoker, baseline);
        }
    }
    
    public void sendRecommendationNotifications() {
        LocalDate baseline = new LocalDate();
        List<User> users = notificationService.getRecommendationNotifications(baseline);

        System system = systemService.getSystem();
        NotificationTemplate template = notificationService.getById(PrismNotificationTemplate.SYSTEM_RECOMMENDATION_NOTIFICATION);

        for (User user : users) {
            notificationService.sendRecommendationNotification(system, user, template, baseline);
        }
    }
    
    private void sendRequestReminders(Scope scope, User invoker, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> definitions = notificationService.getRequestReminders(scope, baseline);
        HashMultimap<String, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO definition : definitions) {
            notificationService.sendRequestReminder(definition, invoker, baseline, sent);
        }
    }
    
    private void sendSyndicatedWorkflowNotifications(Scope scope, User invoker, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> definitions = Lists.newLinkedList();
        definitions.addAll(notificationService.getSyndicatedRequestNotifications(scope, baseline));
        definitions.addAll(notificationService.getSyndicatedUpdateNotifications(scope, baseline));

        Set<User> sent = Sets.newHashSet();

        for (UserNotificationDefinitionDTO definition : definitions) {
            notificationService.sendSyndicatedWorkflowNotification(definition, invoker, baseline, sent);
        }
    }
    
}
