package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration.NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PASSWORD_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_USER_INVITATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserNotification;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

import jersey.repackaged.com.google.common.collect.Maps;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class NotificationService {

    private static Integer requestThrottle = 3;

    @Inject
    private NotificationDAO notificationDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private UserService userService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Inject
    private EntityService entityService;

    @Inject
    private CustomizationService customizationService;

    @Inject
    private ScopeService scopeService;

    @Inject
    private ApplicationContext applicationContext;

    public NotificationDefinition getById(PrismNotificationDefinition id) {
        return entityService.getByProperty(NotificationDefinition.class, "id", id);
    }

    public NotificationConfiguration getNotificationConfiguration(Resource resource, User user, NotificationDefinition definition) {
        return (NotificationConfiguration) customizationService.getConfiguration(NOTIFICATION, resource, definition);
    }

    public List<NotificationDefinition> getDefinitions() {
        return entityService.list(NotificationDefinition.class);
    }

    public List<NotificationDefinition> getWorkflowDefinitions() {
        List<NotificationDefinition> templates = Lists.newLinkedList();
        templates.addAll(notificationDAO.getWorkflowRequestDefinitions());
        templates.addAll(notificationDAO.getWorkflowUpdateDefinitions());
        return templates;
    }

    public void deleteObsoleteNotificationConfigurations() {
        notificationDAO.deleteObsoleteNotificationConfigurations(getWorkflowDefinitions());
    }

    public void sendIndividualWorkflowNotifications(Resource resource, Comment comment) {
        LocalDate baseline = new LocalDate();
        Set<User> exclusions = sendIndividualRequestNotifications(resource, comment, baseline);
        sendIndividualUpdateNotifications(resource, comment, exclusions, baseline);
        entityService.flush();
    }

    public void sendSyndicatedUserNotifications() {

    }

    public void sendNotification(PrismNotificationDefinition notificationTemplateId, NotificationDefinitionDTO notificationDefinitionDTO) {
        NotificationDefinition notificationTemplate = getById(notificationTemplateId);
        sendNotification(notificationTemplate, notificationDefinitionDTO);
    }

    public void sendInvitationNotification(User user, User invitee) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_USER_INVITATION_NOTIFICATION);
        sendNotification(definition, new NotificationDefinitionDTO().withUser(invitee).withAuthor(system.getUser()).withInvoker(user).withResource(system)
                .withTransitionAction(SYSTEM_MANAGE_ACCOUNT));
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome) {
        System system = systemService.getSystem();
        sendNotification(SYSTEM_COMPLETE_REGISTRATION_REQUEST,
                new NotificationDefinitionDTO().withUser(user).withAuthor(system.getUser()).withResource(actionOutcome.getTransitionResource())
                        .withTransitionAction(actionOutcome.getTransitionAction().getId()));
    }

    public void sendResetPasswordNotification(User user, String newPassword) {
        System system = systemService.getSystem();
        sendNotification(SYSTEM_PASSWORD_NOTIFICATION, new NotificationDefinitionDTO().withUser(user).withAuthor(system.getUser()).withResource(system)
                .withTransitionAction(SYSTEM_MANAGE_ACCOUNT).withNewPassword(newPassword));
    }

    public List<PrismNotificationDefinition> getEditableTemplates(PrismScope scope) {
        return (List<PrismNotificationDefinition>) (List<?>) customizationService.getDefinitions(NOTIFICATION, scope);
    }

    public void resetNotifications(User user) {
        notificationDAO.resetNotifications(user);
        for (PrismScope scope : PrismScope.values()) {
            List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope, SYSTEM);
            List<Integer> resourceIds = resourceService.getResources(user, scope, parentScopes).stream().map(a -> a.getId()).collect(Collectors.toList());
            if (!resourceIds.isEmpty()) {
                notificationDAO.resetNotificationsSyndicated(scope, resourceIds);
            }
        }
    }

    public void resetNotifications(UserRole userRole) {
        User user = userRole.getUser();
        Role role = userRole.getRole();
        List<NotificationDefinition> individualDefinitions = notificationDAO.getNotificationDefinitionsIndividual(role);
        notificationDAO.resetNotifications(user, individualDefinitions);
    }

    private Set<User> sendIndividualRequestNotifications(Resource resource, Comment comment, LocalDate baseline) {
        Set<User> recipients = Sets.newHashSet();
        List<UserNotificationDefinitionDTO> requests = notificationDAO.getIndividualRequestDefinitions(resource, baseline);

        if (requests.size() > 0) {
            Map<UserNotificationDTO, Integer> recentRequests = Maps.newHashMap();
            notificationDAO.getRecentNotifications(requests.stream().map(r -> r.getUserId()).collect(toList()), baseline).forEach(un -> {
                recentRequests.put(un, un.getSentCount().intValue());
            });

            for (UserNotificationDefinitionDTO request : requests) {
                User user = userService.getById(request.getUserId());
                if (recentRequests.get(request).intValue() < requestThrottle) {
                    NotificationDefinition definition = getById(request.getNotificationDefinitionId());
                    sendNotification(definition, new NotificationDefinitionDTO().withUser(user).withAuthor(comment.getUser()).withResource(resource).withComment(comment)
                            .withTransitionAction(request.getActionId()));
                    createOrUpdateUserNotification(resource, user, definition, baseline);
                }
                recipients.add(user);
            }
        }

        return recipients;
    }

    private void sendIndividualUpdateNotifications(Resource resource, Comment comment, Set<User> userExclusions, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> updates = notificationDAO.getIndividualUpdateDefinitions(resource, comment.getAction(), userExclusions);
        if (updates.size() > 0) {
            Action action = actionService.getViewEditAction(resource);
            if (action != null) {
                for (UserNotificationDefinitionDTO update : updates) {
                    User user = userService.getById(update.getUserId());
                    NotificationDefinition definition = getById(update.getNotificationDefinitionId());
                    sendNotification(definition, new NotificationDefinitionDTO().withUser(user).withAuthor(comment.getUser()).withResource(resource).withComment(comment)
                            .withTransitionAction(action == null ? null : action.getId()));
                    createOrUpdateUserNotification(resource, user, definition, baseline);
                }
            }
        }
    }

    private void sendNotification(NotificationDefinition template, NotificationDefinitionDTO notificationDefinitionDTO) {
        User user = notificationDefinitionDTO.getUser();
        NotificationConfiguration configuration = getNotificationConfiguration(notificationDefinitionDTO.getResource(), user, template);
        MailMessageDTO message = new MailMessageDTO();

        message.setNotificationConfiguration(configuration);
        message.setNotificationDefinitionDTO(notificationDefinitionDTO);

        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(notificationDefinitionDTO.getResource());
        applicationContext.getBean(MailSender.class).localize(propertyLoader).sendEmail(message);
    }

    public void createOrUpdateUserNotification(Resource resource, User user, NotificationDefinition definition, LocalDate baseline) {
        entityService.createOrUpdate(new UserNotification().withResource(resource).withUser(user).withNotificationDefinition(definition)
                .withLastNotifiedDate(baseline));
    }

}
