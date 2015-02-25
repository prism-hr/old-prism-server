package com.zuehlke.pgadmissions.services;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

@Service
@Transactional
@SuppressWarnings("unchecked")
public class NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
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
        User actionOwner = comment.getActionOwner();
        LocalDate baseline = new LocalDate();

        sendIndividualRequestNotifications(resource, comment, actionOwner, baseline);
        sendIndividualUpdateNotifications(resource, comment, actionOwner, baseline);
    }

    public <T extends Resource> void sendIndividualRequestReminders(Class<T> resourceClass, Integer resourceId, LocalDate baseline) {
        User author = systemService.getSystem().getUser();
        Resource resource = resourceService.getById(resourceClass, resourceId);

        List<UserNotificationDefinitionDTO> definitions = notificationDAO.getIndividualReminderDefinitions(resource, baseline);
        HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO definition : definitions) {
            User user = userService.getById(definition.getUserId());

            Role role = roleService.getById(definition.getRoleId());
            UserRole userRole = roleService.getUserRole(resource, user, role);

            NotificationDefinition notificationDefinition = getById(definition.getNotificationDefinitionId());
            LocalDate lastNotifiedDate = userRole.getLastNotifiedDate();

            Integer reminderInterval = getReminderInterval(resource, user, notificationDefinition);
            LocalDate lastExpectedReminder = baseline.minusDays(reminderInterval);

            if (!sent.get(notificationDefinition).contains(user)
                    && (lastExpectedReminder.isAfter(lastNotifiedDate) || lastExpectedReminder.equals(lastNotifiedDate))) {
                sendNotification(notificationDefinition.getReminderDefinition(), new NotificationDefinitionModelDTO().withUser(user).withAuthor(author)
                        .withResource(resource).withTransitionAction(definition.getActionId()));
                sent.put(notificationDefinition, user);
                userRole.setLastNotifiedDate(baseline);
            }
        }

        resource.setLastRemindedRequestIndividual(baseline);
    }

    public <T extends Resource> void sendSyndicatedRequestNotifications(Class<T> resourceClass, Integer resourceId, LocalDate baseline) {
        User author = systemService.getSystem().getUser();
        Resource resource = resourceService.getById(resourceClass, resourceId);

        List<UserNotificationDefinitionDTO> requests = notificationDAO.getSyndicatedRequestDefinitions(resource, baseline);
        HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

        if (requests.size() > 0) {
            PrismAction transitionActionId = PrismAction.valueOf("SYSTEM_VIEW_" + resource.getResourceScope().name() + "_LIST");

            for (UserNotificationDefinitionDTO definition : requests) {
                User user = userService.getById(definition.getUserId());

                NotificationDefinition notificationDefinition = getById(definition.getNotificationDefinitionId());
                LocalDate lastNotifiedDate = user.getLastNotifiedDate(resource.getClass());

                Integer reminderInterval = getReminderInterval(resource, user, notificationDefinition);
                LocalDate lastExpectedReminder = baseline.minusDays(reminderInterval);
                boolean doSendReminder = lastExpectedReminder.equals(lastNotifiedDate);

                if (!sent.get(notificationDefinition).contains(user)
                        && (lastNotifiedDate == null || lastExpectedReminder.isAfter(lastNotifiedDate) || doSendReminder)) {
                    NotificationDefinition sendTemplate = doSendReminder ? notificationDefinition.getReminderDefinition() : notificationDefinition;
                    sendNotification(sendTemplate, new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(resource)
                            .withTransitionAction(transitionActionId));
                    user.setLastNotifiedDate(resource.getClass(), baseline);
                    sent.put(notificationDefinition, user);
                }
            }
        }

        resource.setLastRemindedRequestSyndicated(baseline);
    }

    public <T extends Resource> void sendSyndicatedUpdateNotifications(Class<T> resourceClass, Integer resourceId, Comment transitionComment, LocalDate baseline) {
        Resource resource = resourceService.getById(resourceClass, resourceId);

        User actionOwner = transitionComment.getActionOwner();
        Action action = transitionComment.getAction();

        List<UserNotificationDefinitionDTO> updates = notificationDAO.getSyndicatedUpdateDefinitions(resource, action, actionOwner, baseline);
        HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

        if (updates.size() > 0) {
            PrismAction transitionActionId = PrismAction.valueOf("SYSTEM_VIEW_" + resource.getResourceScope().name() + "_LIST");

            for (UserNotificationDefinitionDTO update : updates) {
                User user = userService.getById(update.getUserId());
                NotificationDefinition notificationDefinition = getById(update.getNotificationDefinitionId());

                if (!sent.get(notificationDefinition).contains(user)) {
                    sendNotification(notificationDefinition, new NotificationDefinitionModelDTO().withUser(user).withAuthor(actionOwner).withResource(resource)
                            .withTransitionAction(transitionActionId));
                    user.setLastNotifiedDate(resource.getClass(), baseline);
                    sent.put(notificationDefinition, user);
                }
            }
        }

        resource.setLastNotifiedUpdateSyndicated(baseline);
    }

    public List<User> getRecommendationNotifications(LocalDate baseline) {
        return notificationDAO.getRecommendationDefinitions(baseline);
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

    public void sendRecommendationNotification(User transientUser, LocalDate baseline) {
        User persistentUser = userService.getById(transientUser.getId());
        System system = systemService.getSystem();
        NotificationDefinition definition = getById(SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION);
        sendNotification(definition, new NotificationDefinitionModelDTO().withUser(persistentUser).withAuthor(system.getUser()).withResource(system));
        persistentUser.getUserAccount().setLastNotifiedDateApplicationRecommendation(baseline);
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

    private void sendIndividualRequestNotifications(Resource resource, Comment comment, User author, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> requests = notificationDAO.getIndividualRequestDefinitions(resource, author, baseline);
        HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO request : requests) {
            User user = userService.getById(request.getUserId());
            NotificationDefinition definition = getById(request.getNotificationDefinitionId());

            if (!sent.get(definition).contains(user)) {
                sendNotification(definition, new NotificationDefinitionModelDTO().withUser(user).withAuthor(author).withResource(resource).withComment(comment)
                        .withTransitionAction(request.getActionId()));
                sent.put(definition, user);
            }

            Role role = roleService.getById(request.getRoleId());
            UserRole userRole = roleService.getUserRole(resource, user, role);
            userRole.setLastNotifiedDate(baseline);
        }
    }

    private void sendIndividualUpdateNotifications(Resource resource, Comment comment, User author, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> updates = notificationDAO.getIndividualUpdateDefinitions(resource, comment.getAction(), author, baseline);
        HashMultimap<NotificationDefinition, User> sent = HashMultimap.create();

        if (updates.size() > 0) {
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

}
