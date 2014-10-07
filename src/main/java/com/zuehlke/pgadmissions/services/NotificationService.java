package com.zuehlke.pgadmissions.services;

import java.util.List;
import java.util.Map;

import com.zuehlke.pgadmissions.rest.dto.NotificationConfigurationDTO;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplateProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplatePropertyCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Value("${application.host}")
    private String host;

    @Autowired
    private NotificationDAO notificationDAO;

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
    private ConfigurationService configurationService;

    @Autowired
    private NotificationTemplatePropertyService notificationTemplatePropertyService;

    public NotificationTemplate getById(PrismNotificationTemplate id) {
        return entityService.getByProperty(NotificationTemplate.class, "id", id);
    }

    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return configurationService.getConfiguration(NotificationConfiguration.class, resource, "notificationTemplate", template);
    }

    public Integer getReminderInterval(Resource resource, NotificationTemplate template) {
        NotificationConfiguration configuration = getConfiguration(resource, template);
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

        sendIndividualRequestNotifications(resource, invoker, baseline);
        sendIndividualUpdateNotifications(resource, comment.getAction(), invoker, baseline);
    }

    public <T extends Resource> void sendIndividualRequestReminders(Class<T> resourceClass, Integer resourceId, LocalDate baseline) {
        User invoker = systemService.getSystem().getUser();
        Resource resource = resourceService.getById(resourceClass, resourceId);

        List<UserNotificationDefinitionDTO> reminders = notificationDAO.getIndividualRequestReminders(resource, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO reminder : reminders) {
            User user = userService.getById(reminder.getUserId());

            Role role = roleService.getById(reminder.getRoleId());
            UserRole userRole = roleService.getUserRole(resource, user, role);

            NotificationTemplate notificationTemplate = getById(reminder.getNotificationTemplateId());
            Integer reminderInterval = getReminderInterval(resource, notificationTemplate);

            if (!sent.get(notificationTemplate).contains(user) && baseline.minusDays(reminderInterval) == userRole.getLastNotifiedDate()) {
                sendNotification(notificationTemplate.getReminderTemplate(), new NotificationTemplateModelDTO(user, resource, invoker).withTransitionAction(reminder.getActionId()));
                sent.put(notificationTemplate, user);
            }

            userRole.setNotificationTemplate(notificationTemplate);
            userRole.setLastNotifiedDate(baseline);
        }
    }

    public <T extends Resource> void sendSyndicatedRequestNotifications(Class<T> resourceClass, Integer resourceId, LocalDate baseline) {
        User invoker = systemService.getSystem().getUser();
        Resource resource = resourceService.getById(resourceClass, resourceId);

        List<UserNotificationDefinitionDTO> definitions = notificationDAO.getSyndicatedRequestNotifications(resource, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO definition : definitions) {
            User user = userService.getById(definition.getUserId());

            NotificationTemplate notificationTemplate = getById(definition.getNotificationTemplateId());
            LocalDate lastNotifiedDate = user.getLastNotifiedDate(resource.getClass());

            Integer reminderInterval = getReminderInterval(resource, notificationTemplate);
            boolean doSendReminder = baseline.minusDays(reminderInterval) == lastNotifiedDate;

            if (!sent.get(notificationTemplate).contains(user) && (lastNotifiedDate == null || doSendReminder)) {
                NotificationTemplate sendTemplate = doSendReminder ? notificationTemplate.getReminderTemplate() : notificationTemplate;
                sendNotification(sendTemplate, new NotificationTemplateModelDTO(user, resource, invoker).withTransitionAction(definition.getActionId()));
                user.setLastNotifiedDate(resource.getClass(), baseline);
                sent.put(notificationTemplate, user);
            }
        }
    }

    public <T extends Resource> void sendSyndicatedUpdateNotifications(Class<T> resourceClass, Integer resourceId, Comment transitionComment, LocalDate baseline) {
        User invoker = transitionComment.getAuthor();
        Resource resource = resourceService.getById(resourceClass, resourceId);

        State state = transitionComment.getState();
        Action action = transitionComment.getAction();

        List<UserNotificationDefinitionDTO> updates = notificationDAO.getSyndicatedUpdateNotifications(resource, state, action, invoker, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO update : updates) {
            User user = userService.getById(update.getUserId());
            NotificationTemplate notificationTemplate = getById(update.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(notificationTemplate, new NotificationTemplateModelDTO(user, resource, invoker));
                user.setLastNotifiedDate(resource.getClass(), baseline);
                sent.put(notificationTemplate, user);
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
        for (User user : userService.getUsersForResourceAndRole(institution, PrismRole.INSTITUTION_ADMINISTRATOR)) {
            NotificationTemplate template = getById(PrismNotificationTemplate.INSTITUTION_IMPORT_ERROR_NOTIFICATION);
            sendNotification(template, new NotificationTemplateModelDTO(user, institution, system.getUser()).withErrorMessage(errorMessage));
        }
    }

    public void sendRecommendationNotification(User transientUser, LocalDate baseline) {
        User persistentUser = userService.getById(transientUser.getId());
        System system = systemService.getSystem();
        NotificationTemplate template = getById(PrismNotificationTemplate.SYSTEM_RECOMMENDATION_NOTIFICATION);
        String recommendations = advertService.getRecommendedAdvertsForEmail(persistentUser);
        sendNotification(template, new NotificationTemplateModelDTO(persistentUser, system, system.getUser()).withRecommendations(recommendations));
        persistentUser.getUserAccount().setLastNotifiedDateRecommendation(baseline);
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome) {
        System system = systemService.getSystem();
        sendNotification(PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST,
                new NotificationTemplateModelDTO(user, actionOutcome.getTransitionResource(), system.getUser()).withTransitionAction(actionOutcome.getTransitionAction().getId()));
    }

    public void sendResetPasswordNotification(User user, String newPassword) {
        System system = systemService.getSystem();

        sendNotification(PrismNotificationTemplate.SYSTEM_PASSWORD_NOTIFICATION, new NotificationTemplateModelDTO(user, systemService.getSystem(), system.getUser()).withNewPassword(newPassword));
    }

    private void sendIndividualRequestNotifications(Resource resource, User invoker, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> requests = notificationDAO.getIndividualRequestNotifications(resource, invoker);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO request : requests) {
            User user = userService.getById(request.getUserId());
            NotificationTemplate notificationTemplate = getById(request.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(notificationTemplate, new NotificationTemplateModelDTO(user, resource, invoker).withTransitionAction(request.getActionId()));
                sent.put(notificationTemplate, user);
            }

            Role role = roleService.getById(request.getRoleId());
            UserRole userRole = roleService.getUserRole(resource, user, role);
            userRole.setNotificationTemplate(notificationTemplate);
            userRole.setLastNotifiedDate(baseline);
        }
    }

    private void sendIndividualUpdateNotifications(Resource resource, Action action, User sender, LocalDate baseline) {
        State state = resource.getPreviousState();

        List<UserNotificationDefinitionDTO> updates = notificationDAO.getIndividualUpdateNotifications(resource, state, action, sender, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO update : updates) {
            User user = userService.getById(update.getUserId());
            NotificationTemplate notificationTemplate = getById(update.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(notificationTemplate, new NotificationTemplateModelDTO(user, resource, sender));
                sent.put(notificationTemplate, user);
            }
        }
    }

    private void sendNotification(NotificationTemplate template, NotificationTemplateModelDTO modelDTO) {
        NotificationConfiguration configuration = getConfiguration(modelDTO.getResource(), template);
        MailMessageDTO message = new MailMessageDTO();

        message.setConfiguration(configuration);
        message.setModelDTO(modelDTO);
        message.setAttachments(Lists.<AttachmentInputSource>newArrayList());

        mailSender.sendEmail(message);
    }

    public List<PrismNotificationTemplate> getEditableTemplates(PrismScope scope) {
        return notificationDAO.geEditableTemplates(scope);
    }

    public void saveConfiguration(Resource resource, NotificationTemplate template, NotificationConfigurationDTO notificationConfigurationDTO) {
        NotificationConfiguration configuration = getConfiguration(resource, template);
        configuration.setSubject(notificationConfigurationDTO.getSubject());
        configuration.setContent(notificationConfigurationDTO.getContent());
        configuration.setReminderInterval(notificationConfigurationDTO.getReminderInterval());
    }
}
