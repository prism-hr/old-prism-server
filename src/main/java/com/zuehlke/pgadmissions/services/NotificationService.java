package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.INSTITUTION_IMPORT_ERROR_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_REQUEST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_INVITATION_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_PASSWORD_NOTIFICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserNotification;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.domain.workflow.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.workflow.NotificationDefinition;
import com.zuehlke.pgadmissions.domain.workflow.Role;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class NotificationService {

    @Inject
    private NotificationDAO notificationDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private UserService userService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

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

    public Integer getReminderInterval(Resource resource, User user, NotificationDefinition definition) {
        NotificationConfiguration configuration = getNotificationConfiguration(resource, user, definition);
        return configuration == null ? 1 : configuration.getReminderInterval();
    }

    public NotificationConfiguration getNotificationConfiguration(Resource resource, User user, NotificationDefinition definition) {
        return (NotificationConfiguration) customizationService.getConfiguration(PrismConfiguration.NOTIFICATION, resource, user, definition);
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

    public void sendWorkflowNotifications(Resource resource, Comment comment) {
        User author = systemService.getSystem().getUser();
        LocalDate baseline = new LocalDate();
        Set<User> exclusions = sendIndividualRequestNotifications(resource, comment, author, baseline);
        sendIndividualUpdateNotifications(resource, comment, author, exclusions, baseline);
    }

    public void sendIndividualRequestReminders(PrismScope resourceScope, Integer resourceId, LocalDate baseline) {
        User author = systemService.getSystem().getUser();
        Resource resource = resourceService.getById(resourceScope, resourceId);

        List<UserNotificationDefinitionDTO> definitions = notificationDAO.getIndividualReminderDefinitions(resource, baseline);

        if (definitions.size() > 0) {
            HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

            for (UserNotificationDefinitionDTO definition : definitions) {
                User user = userService.getById(definition.getUserId());
                Role role = roleService.getById(definition.getRoleId());
                UserRole userRole = roleService.getUserRole(resource.getEnclosingResource(role.getScope().getId()), user, role);

                NotificationDefinition notificationDefinition = getById(definition.getNotificationDefinitionId());
                UserNotification userNotification = notificationDAO.getUserNotification(resource, userRole, notificationDefinition);
                LocalDate lastNotifiedDate = userNotification.getLastNotifiedDate();

                Integer reminderInterval = getReminderInterval(resource, user, notificationDefinition);
                LocalDate lastExpectedReminder = baseline.minusDays(reminderInterval);

                if ((lastExpectedReminder.isAfter(lastNotifiedDate) || lastExpectedReminder.equals(lastNotifiedDate))) {
                    if (!sent.get(notificationDefinition).contains(user)) {
                        sendNotification(notificationDefinition.getReminderDefinition(), new NotificationDefinitionModelDTO().withUser(user).withAuthor(author)
                                .withResource(resource).withTransitionAction(definition.getActionId()));
                        sent.put(notificationDefinition, user);
                    }
                    userNotification.setLastNotifiedDate(baseline);
                }
            }
        }

        resource.setLastRemindedRequestIndividual(baseline);
    }

    public void sendSyndicatedRequestNotifications(PrismScope resourceScope, Integer resourceId, LocalDate baseline) {
        System system = systemService.getSystem();
        Resource resource = resourceService.getById(resourceScope, resourceId);

        List<UserNotificationDefinitionDTO> requests = notificationDAO.getSyndicatedRequestDefinitions(resource, baseline);

        if (requests.size() > 0) {
            HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

            User author = system.getUser();
            PrismAction transitionActionId = PrismAction.valueOf("SYSTEM_VIEW_" + resource.getResourceScope().name() + "_LIST");

            for (UserNotificationDefinitionDTO request : requests) {
                User user = userService.getById(request.getUserId());
                Role role = roleService.getById(request.getRoleId());
                UserRole userRole = roleService.getUserRole(resource.getEnclosingResource(role.getScope().getId()), user, role);

                NotificationDefinition definition = getById(request.getNotificationDefinitionId());
                UserNotification userNotification = notificationDAO.getUserNotification(system, userRole, definition);
                LocalDate lastNotifiedDate = userNotification == null ? null : userNotification.getLastNotifiedDate();

                Integer reminderInterval = getReminderInterval(resource, user, definition);
                LocalDate lastExpectedReminder = baseline.minusDays(reminderInterval);
                boolean doSendReminder = lastExpectedReminder.equals(lastNotifiedDate);

                if ((lastNotifiedDate == null || lastExpectedReminder.isAfter(lastNotifiedDate) || doSendReminder)) {
                    NotificationDefinition sendDefinition = doSendReminder ? definition.getReminderDefinition() : definition;
                    if (!sent.get(definition).contains(user)) {
                        sendNotification(sendDefinition, new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(resource)
                                .withTransitionAction(transitionActionId));
                        sent.put(definition, user);
                    }
                    createOrUpdateUserNotification(system, userRole, sendDefinition, baseline);
                }
            }
        }

        resource.setLastRemindedRequestSyndicated(baseline);
    }

    public void sendSyndicatedUpdateNotifications(PrismScope resourceScope, Integer resourceId, Comment transitionComment, LocalDate baseline) {
        System system = systemService.getSystem();
        Resource resource = resourceService.getById(resourceScope, resourceId);

        Action action = transitionComment.getAction();
        User invoker = transitionComment.getActionOwner();

        List<UserNotificationDefinitionDTO> updates = notificationDAO.getSyndicatedUpdateDefinitions(resource, action, invoker, baseline);

        if (updates.size() > 0) {
            Set<User> requested = Sets.newHashSet();
            HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

            User author = system.getUser();
            PrismAction transitionActionId = PrismAction.valueOf("SYSTEM_VIEW_" + resource.getResourceScope().name() + "_LIST");

            for (UserNotificationDefinitionDTO update : updates) {
                User user = userService.getById(update.getUserId());

                if (!requested.contains(user)) {
                    List<Integer> requests = notificationDAO.getRecentSyndicatedUserNotifications(system, user, baseline);

                    if (requests.isEmpty()) {
                        Role role = roleService.getById(update.getRoleId());
                        UserRole userRole = roleService.getUserRole(resource.getEnclosingResource(role.getScope().getId()), user, role);

                        NotificationDefinition definition = getById(update.getNotificationDefinitionId());
                        if (!sent.get(definition).contains(user)) {
                            sendNotification(definition, new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(resource)
                                    .withTransitionAction(transitionActionId));
                            sent.put(definition, user);
                        }
                        createOrUpdateUserNotification(system, userRole, definition, baseline);
                    }
                    else {
                        requested.add(user);
                    }
                }
            }
        }

        resource.setLastNotifiedUpdateSyndicated(baseline);
    }

    public void sendRecommendationNotifications(Integer userId, LocalDate baseline, LocalDate lastRecommendedBaseline) {
        List<UserNotificationDefinitionDTO> recommends = notificationDAO.getRecommendationDefinitions(userId, lastRecommendedBaseline);

        if (recommends.size() > 0) {
            System system = systemService.getSystem();
            User author = system.getUser();
            NotificationDefinition definition = getById(SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION);
            
            int sent = 0;
            for (UserNotificationDefinitionDTO recommend : recommends) {
                User user = userService.getById(userId);
                Application application = applicationService.getById(recommend.getResourceId());

                if (sent == 0) {
                    sendNotification(definition, new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(system)
                            .withTransitionAction(SYSTEM_MANAGE_ACCOUNT));
                    sent++;
                }
                
                Role role = roleService.getById(recommend.getRoleId());
                UserRole userRole = roleService.getUserRole(application, user, role);
                createOrUpdateUserNotification(application, userRole, definition, baseline);
            }
        }
    }

    public void sendNotification(PrismNotificationDefinition notificationTemplateId, NotificationDefinitionModelDTO modelDTO) {
        NotificationDefinition notificationTemplate = getById(notificationTemplateId);
        sendNotification(notificationTemplate, modelDTO);
    }

    public void sendDataImportErrorNotifications(Institution institution, String errorMessage) {
        System system = systemService.getSystem();
        for (User user : userService.getUsersForResourceAndRoles(institution, INSTITUTION_ADMINISTRATOR)) {
            NotificationDefinition template = getById(INSTITUTION_IMPORT_ERROR_NOTIFICATION);
            sendNotification(template, new NotificationDefinitionModelDTO().withUser(user).withAuthor(system.getUser()).withResource(institution)
                    .withDataImportErrorMessage(errorMessage));
        }
    }

    public void sendInvitationNotifications(User user, User invitee) {
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_INVITATION_NOTIFICATION);
        sendNotification(definition, new NotificationDefinitionModelDTO().withUser(invitee).withAuthor(system.getUser()).withInvoker(user).withResource(system)
                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
    }

    public void sendInvitationNotifications(Comment comment) {
        for (CommentAssignedUser assignee : comment.getAssignedUsers()) {
            User invitee = assignee.getUser();
            if (assignee.getRoleTransitionType() == CREATE && invitee.getUserAccount() == null) {
                sendInvitationNotifications(comment.getUser(), invitee);
            }
        }
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome) {
        sendRegistrationNotification(user, actionOutcome, null);
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome, Comment comment) {
        System system = systemService.getSystem();
        sendNotification(SYSTEM_COMPLETE_REGISTRATION_REQUEST,
                new NotificationDefinitionModelDTO().withUser(user).withAuthor(system.getUser()).withResource(actionOutcome.getTransitionResource())
                        .withComment(comment).withTransitionAction(actionOutcome.getTransitionAction().getId()));
    }

    public void sendResetPasswordNotification(User user, String newPassword) {
        System system = systemService.getSystem();
        sendNotification(SYSTEM_PASSWORD_NOTIFICATION, new NotificationDefinitionModelDTO().withUser(user).withAuthor(system.getUser()).withResource(system)
                .withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST).withNewPassword(newPassword));
    }

    public List<PrismNotificationDefinition> getEditableTemplates(PrismScope scope) {
        return (List<PrismNotificationDefinition>) (List<?>) customizationService.getDefinitions(PrismConfiguration.NOTIFICATION, scope);
    }

    public void resetNotifications(Resource resource) {
        resource.setLastRemindedRequestIndividual(null);
        resource.setLastRemindedRequestSyndicated(null);
        resource.setLastNotifiedUpdateSyndicated(null);
    }

    public void resetNotifications(User user) {
        notificationDAO.resetNotificationsIndividual(user);
        for (PrismScope scope : PrismScope.values()) {
            List<PrismScope> parentScopes = scopeService.getParentScopesDescending(scope);
            Set<Integer> assignedResources = resourceService.getAssignedResources(user, scope, parentScopes);
            if (!assignedResources.isEmpty()) {
                notificationDAO.resetNotificationsSyndicated(scope, assignedResources);
            }
            PrismReflectionUtils.setProperty(user, "lastNotifiedDate" + scope.getUpperCamelName(), null);
        }
    }

    private Set<User> sendIndividualRequestNotifications(Resource resource, Comment comment, User author, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> requests = notificationDAO.getIndividualRequestDefinitions(resource, author, baseline);

        Set<User> recipients = Sets.newHashSet();

        if (requests.size() > 0) {
            HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

            for (UserNotificationDefinitionDTO request : requests) {
                User user = userService.getById(request.getUserId());
                NotificationDefinition definition = getById(request.getNotificationDefinitionId());

                if (!sent.get(definition).contains(user)) {
                    sendNotification(definition,
                            new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(resource).withComment(comment)
                                    .withTransitionAction(request.getActionId()));
                    recipients.add(user);
                    sent.put(definition, user);
                }

                Role role = roleService.getById(request.getRoleId());
                UserRole userRole = roleService.getUserRole(resource.getEnclosingResource(role.getScope().getId()), user, role);
                createOrUpdateUserNotification(resource, userRole, definition, baseline);
            }
        }

        return recipients;
    }

    private void sendIndividualUpdateNotifications(Resource resource, Comment comment, User author, Set<User> exclusions, LocalDate baseline) {
        exclusions.add(author);
        List<UserNotificationDefinitionDTO> updates = notificationDAO.getIndividualUpdateDefinitions(resource, comment.getAction(), exclusions, baseline);

        if (updates.size() > 0) {
            HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();
            PrismAction transitionActionId = actionService.getViewEditAction(resource).getId();

            for (UserNotificationDefinitionDTO update : updates) {
                User user = userService.getById(update.getUserId());
                NotificationDefinition definition = getById(update.getNotificationDefinitionId());

                if (!sent.get(definition).contains(user)) {
                    sendNotification(definition,
                            new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(resource).withComment(comment)
                                    .withTransitionAction(transitionActionId));
                    sent.put(definition, user);
                }
            }
        }
    }

    private void sendNotification(NotificationDefinition template, NotificationDefinitionModelDTO modelDTO) {
        User user = modelDTO.getUser();
        NotificationConfiguration configuration = getNotificationConfiguration(modelDTO.getResource(), user, template);
        MailMessageDTO message = new MailMessageDTO();

        message.setConfiguration(configuration);
        message.setModelDTO(modelDTO);

        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).localize(modelDTO.getResource());
        applicationContext.getBean(MailSender.class).localize(propertyLoader).sendEmail(message);
    }

    public void createOrUpdateUserNotification(Resource resource, UserRole userRole, NotificationDefinition definition, LocalDate baseline) {
        UserNotification transientUserNotification = new UserNotification().withResource(resource).withUserRole(userRole)
                .withNotificationDefinition(definition);
        UserNotification persistentUserNotification = entityService.getDuplicateEntity(transientUserNotification);
        if (persistentUserNotification == null) {
            transientUserNotification.setLastNotifiedDate(baseline);
            userRole.getUserNotifications().add(transientUserNotification);
            entityService.save(transientUserNotification);
        } else {
            persistentUserNotification.setLastNotifiedDate(baseline);
            entityService.equals(transientUserNotification);
        }
    }
}
