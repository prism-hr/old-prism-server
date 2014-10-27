package com.zuehlke.pgadmissions.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserRole;
import com.zuehlke.pgadmissions.domain.workflow.*;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.SYSTEM_VIEW_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate.*;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.INSTITUTION_ADMINISTRATOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

@Service
@Transactional
public class NotificationService {

    @Value("${application.host}")
    private String host;

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
    private MailSender mailSender;

    @Autowired
    private EntityService entityService;

    @Autowired
    private CustomizationService customizationService;

    public NotificationTemplate getById(PrismNotificationTemplate id) {
        return entityService.getByProperty(NotificationTemplate.class, "id", id);
    }

    public NotificationConfiguration getConfiguration(Resource resource, User user, NotificationTemplate template) {
        return customizationService.getConfiguration(NotificationConfiguration.class, resource, user, "notificationTemplate", template);
    }

    public NotificationConfiguration getConfigurationStrict(Resource resource, PrismLocale locale, PrismProgramType programType, NotificationTemplate template) {
        return customizationService.getConfigurationStrict(NotificationConfiguration.class, resource, locale, programType, "notificationTemplate", template);
    }

    public NotificationConfiguration createConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, NotificationTemplate template,
                                                         String subject, String content, Integer reminderInterval) {
        return new NotificationConfiguration().withResource(resource).withLocale(locale).withProgramType(programType).withNotificationTemplate(template)
                .withSubject(subject).withContent(content).withReminderInterval(reminderInterval)
                .withSystemDefault(customizationService.isSystemDefault(template, locale, programType));
    }

    public void updateConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, NotificationTemplate template,
                                    NotificationConfigurationDTO notificationConfigurationDTO) throws DeduplicationException {
        NotificationConfiguration configuration = new NotificationConfiguration().withResource(resource).withLocale(locale).withProgramType(programType)
                .withNotificationTemplate(template).withSubject(notificationConfigurationDTO.getSubject())
                .withContent(notificationConfigurationDTO.getContent()).withReminderInterval(notificationConfigurationDTO.getReminderInterval());
        entityService.createOrUpdate(configuration);
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_UPDATED_NOTIFICATION"));
    }

    public void restoreDefaultConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, NotificationTemplate template)
            throws DeduplicationException {
        customizationService.restoreDefaultConfiguration(NotificationConfiguration.class, resource, locale, programType, "notificationTemplate", template);
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_RESTORED_NOTIFICATION_DEFAULT"));
    }

    public void restoreGlobalConfiguration(Resource resource, PrismLocale locale, PrismProgramType programType, NotificationTemplate template)
            throws DeduplicationException {
        customizationService.restoreGlobalConfiguration(NotificationConfiguration.class, resource, locale, programType, "notificationTemplate", template);
        resourceService.executeUpdate(resource, PrismDisplayProperty.valueOf(resource.getResourceScope().name() + "_COMMENT_RESTORED_NOTIFICATION_GLOBAL"));
    }

    public Integer getReminderInterval(Resource resource, User user, NotificationTemplate template) {
        NotificationConfiguration configuration = getConfiguration(resource, user, template);
        return configuration == null ? 1 : configuration.getReminderInterval();
    }

    public List<NotificationTemplate> getTemplates() {
        return entityService.list(NotificationTemplate.class);
    }

    public List<NotificationTemplate> getWorkflowTemplates() {
        List<NotificationTemplate> templates = Lists.newLinkedList();
        templates.addAll(notificationDAO.getWorkflowRequestTemplates());
        templates.addAll(notificationDAO.getWorkflowUpdateTemplates());
        return templates;
    }

    public void deleteObsoleteNotificationConfigurations() {
        notificationDAO.deleteObsoleteNotificationConfigurations(getWorkflowTemplates());
    }

    public void sendWorkflowNotifications(Resource resource, Comment comment) {
        User invoker = comment.getAuthor();
        LocalDate baseline = new LocalDate();

        sendIndividualRequestNotifications(resource, comment, invoker, baseline);
        sendIndividualUpdateNotifications(resource, comment, invoker, baseline);
    }

    public <T extends Resource> void sendIndividualRequestReminders(Class<T> resourceClass, Integer resourceId, LocalDate baseline) {
        User author = systemService.getSystem().getUser();
        Resource resource = resourceService.getById(resourceClass, resourceId);

        List<UserNotificationDefinitionDTO> reminders = notificationDAO.getIndividualRequestReminders(resource, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO reminder : reminders) {
            User user = userService.getById(reminder.getUserId());

            Role role = roleService.getById(reminder.getRoleId());
            UserRole userRole = roleService.getUserRole(resource, user, role);

            NotificationTemplate notificationTemplate = getById(reminder.getNotificationTemplateId());
            Integer reminderInterval = getReminderInterval(resource, user, notificationTemplate);

            if (!sent.get(notificationTemplate).contains(user) && baseline.minusDays(reminderInterval) == userRole.getLastNotifiedDate()) {
                sendNotification(notificationTemplate.getReminderTemplate(),
                        new NotificationTemplateModelDTO().withUser(user).withAuthor(author).withResource(resource)
                                .withTransitionAction(reminder.getActionId()));
                sent.put(notificationTemplate, user);
            }

            userRole.setLastNotifiedDate(baseline);
        }
    }

    public <T extends Resource> void sendSyndicatedRequestNotifications(Class<T> resourceClass, Integer resourceId, LocalDate baseline) {
        User author = systemService.getSystem().getUser();
        Resource resource = resourceService.getById(resourceClass, resourceId);

        List<UserNotificationDefinitionDTO> requests = notificationDAO.getSyndicatedRequestNotifications(resource, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        if (requests.size() > 0) {
            PrismAction transitionActionId = PrismAction.valueOf("SYSTEM_VIEW_" + resource.getResourceScope().name() + "_LIST");

            for (UserNotificationDefinitionDTO definition : requests) {
                User user = userService.getById(definition.getUserId());

                NotificationTemplate notificationTemplate = getById(definition.getNotificationTemplateId());
                LocalDate lastNotifiedDate = user.getLastNotifiedDate(resource.getClass());

                Integer reminderInterval = getReminderInterval(resource, user, notificationTemplate);
                boolean doSendReminder = baseline.minusDays(reminderInterval) == lastNotifiedDate;

                if (!sent.get(notificationTemplate).contains(user) && (lastNotifiedDate == null || doSendReminder)) {
                    NotificationTemplate sendTemplate = doSendReminder ? notificationTemplate.getReminderTemplate() : notificationTemplate;
                    sendNotification(sendTemplate, new NotificationTemplateModelDTO().withUser(user).withAuthor(author).withResource(resource)
                            .withTransitionAction(transitionActionId));
                    user.setLastNotifiedDate(resource.getClass(), baseline);
                    sent.put(notificationTemplate, user);
                }
            }
        }
    }

    public <T extends Resource> void sendSyndicatedUpdateNotifications(Class<T> resourceClass, Integer resourceId, Comment transitionComment, LocalDate baseline) {
        User author = transitionComment.getAuthor();
        Resource resource = resourceService.getById(resourceClass, resourceId);

        State state = transitionComment.getState();
        Action action = transitionComment.getAction();

        List<UserNotificationDefinitionDTO> updates = notificationDAO.getSyndicatedUpdateNotifications(resource, state, action, author, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        if (updates.size() > 0) {
            PrismAction transitionActionId = PrismAction.valueOf("SYSTEM_VIEW_" + resource.getResourceScope().name() + "_LIST");

            for (UserNotificationDefinitionDTO update : updates) {
                User user = userService.getById(update.getUserId());
                NotificationTemplate notificationTemplate = getById(update.getNotificationTemplateId());

                if (!sent.get(notificationTemplate).contains(user)) {
                    sendNotification(notificationTemplate, new NotificationTemplateModelDTO().withUser(user).withAuthor(author).withResource(resource)
                            .withTransitionAction(transitionActionId));
                    user.setLastNotifiedDate(resource.getClass(), baseline);
                    sent.put(notificationTemplate, user);
                }
            }
        }
    }

    public List<User> getRecommendationNotifications(LocalDate baseline) {
        return notificationDAO.getRecommendationNotifications(baseline);
    }

    public void sendNotification(PrismNotificationTemplate notificationTemplateId, NotificationTemplateModelDTO modelDTO) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(notificationTemplate, modelDTO);
    }

    public void sendDataImportErrorNotifications(Institution institution, String errorMessage) {
        System system = systemService.getSystem();
        for (User user : userService.getUsersForResourceAndRole(institution, INSTITUTION_ADMINISTRATOR)) {
            NotificationTemplate template = getById(INSTITUTION_IMPORT_ERROR_NOTIFICATION);
            sendNotification(template, new NotificationTemplateModelDTO().withUser(user).withAuthor(system.getUser()).withResource(institution)
                    .withDataImportErrorMessage(errorMessage));
        }
    }

    public void sendRecommendationNotification(User transientUser, LocalDate baseline) {
        User persistentUser = userService.getById(transientUser.getId());
        System system = systemService.getSystem();
        NotificationTemplate template = getById(SYSTEM_APPLICATION_RECOMMENDATION_NOTIFICATION);
        sendNotification(template, new NotificationTemplateModelDTO().withUser(persistentUser).withAuthor(system.getUser()).withResource(system));
        persistentUser.getUserAccount().setLastNotifiedDateApplicationRecommendation(baseline);
    }

    public void sendInvitationNotifications(Comment comment) {
        NotificationTemplate template = getById(SYSTEM_INVITATION_NOTIFICATION);
        System system = systemService.getSystem();

        for (CommentAssignedUser assignee : comment.getAssignedUsers()) {
            User invitee = assignee.getUser();
            if (assignee.getRoleTransitionType() == CREATE && invitee.getUserAccount() == null) {
                sendNotification(template, new NotificationTemplateModelDTO().withUser(invitee).withAuthor(system.getUser()).withInvoker(comment.getUser())
                        .withResource(system).withTransitionAction(SYSTEM_VIEW_APPLICATION_LIST));
            }
        }
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome) {
        sendRegistrationNotification(user, actionOutcome, null);
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome, Comment comment) {
        System system = systemService.getSystem();
        sendNotification(
                SYSTEM_COMPLETE_REGISTRATION_REQUEST,
                new NotificationTemplateModelDTO().withUser(user).withAuthor(system.getUser()).withResource(actionOutcome.getTransitionResource())
                        .withComment(comment).withTransitionAction(actionOutcome.getTransitionAction().getId()));
    }

    public void sendResetPasswordNotification(User user, String newPassword) {
        System system = systemService.getSystem();
        sendNotification(SYSTEM_PASSWORD_NOTIFICATION, new NotificationTemplateModelDTO().withUser(user).withAuthor(system.getUser()).withResource(system)
                .withNewPassword(newPassword));
    }

    public List<PrismNotificationTemplate> getEditableTemplates(PrismScope scope) {
        return notificationDAO.geEditableTemplates(scope);
    }

    private void sendIndividualRequestNotifications(Resource resource, Comment comment, User author, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> requests = notificationDAO.getIndividualRequestNotifications(resource, author, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO request : requests) {
            User user = userService.getById(request.getUserId());
            NotificationTemplate notificationTemplate = getById(request.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(
                        notificationTemplate,
                        new NotificationTemplateModelDTO().withUser(user).withAuthor(author).withResource(resource).withComment(comment)
                                .withTransitionAction(request.getActionId()));
                sent.put(notificationTemplate, user);
            }

            Role role = roleService.getById(request.getRoleId());
            UserRole userRole = roleService.getUserRole(resource, user, role);
            userRole.setLastNotifiedDate(baseline);
        }
    }

    private void sendIndividualUpdateNotifications(Resource resource, Comment comment, User author, LocalDate baseline) {
        State state = resource.getPreviousState();

        List<UserNotificationDefinitionDTO> updates = notificationDAO.getIndividualUpdateNotifications(resource, state, comment.getAction(), author, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        if (updates.size() > 0) {
            PrismAction transitionActionId = actionService.getViewEditAction(resource).getId();

            for (UserNotificationDefinitionDTO update : updates) {
                User user = userService.getById(update.getUserId());
                NotificationTemplate notificationTemplate = getById(update.getNotificationTemplateId());

                if (!sent.get(notificationTemplate).contains(user)) {
                    sendNotification(notificationTemplate, new NotificationTemplateModelDTO().withUser(user).withAuthor(author).withResource(resource)
                            .withComment(comment).withTransitionAction(transitionActionId));
                    sent.put(notificationTemplate, user);
                }
            }
        }
    }

    private void sendNotification(NotificationTemplate template, NotificationTemplateModelDTO modelDTO) {
        Preconditions.checkNotNull(template);
        Preconditions.checkNotNull(modelDTO);

        NotificationConfiguration configuration = getConfiguration(modelDTO.getResource(), modelDTO.getUser(), template);
        Preconditions.checkNotNull(configuration, "Could not find configuration for template " + template.getId() + ", resource " + modelDTO.getResource() + ", user " + modelDTO.getUser().getEmail());
        MailMessageDTO message = new MailMessageDTO();

        message.setConfiguration(configuration);
        message.setModelDTO(modelDTO);
        message.setAttachments(Lists.<AttachmentInputSource>newArrayList());

        mailSender.sendEmail(message);
    }

}
