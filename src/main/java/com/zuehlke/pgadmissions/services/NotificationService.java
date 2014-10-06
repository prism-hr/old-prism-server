package com.zuehlke.pgadmissions.services;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.definitions.workflow.*;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.dto.MailMessageDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.services.builders.pdf.mail.AttachmentInputSource;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplateProperty.*;

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

    public NotificationTemplate getById(PrismNotificationTemplate id) {
        return entityService.getByProperty(NotificationTemplate.class, "id", id);
    }

    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return notificationDAO.getConfiguration(resource, template);
    }

    public NotificationTemplateVersion getActiveVersion(Resource resource, NotificationTemplate template) {
        NotificationConfiguration configuration = notificationDAO.getConfiguration(resource, template);
        return configuration == null ? null : configuration.getNotificationTemplateVersion();
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
                sendNotification(user, resource, notificationTemplate.getReminderTemplate(), invoker, reminder.getActionId(), Collections.<PrismNotificationTemplateProperty, String>emptyMap());
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
                sendNotification(user, resource, sendTemplate, invoker, definition.getActionId(), Collections.<PrismNotificationTemplateProperty, String>emptyMap());
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
                sendNotification(user, resource, notificationTemplate, invoker, null, Collections.<PrismNotificationTemplateProperty, String>emptyMap());
                user.setLastNotifiedDate(resource.getClass(), baseline);
                sent.put(notificationTemplate, user);
            }
        }
    }

    public List<User> getRecommendationNotifications(LocalDate baseline) {
        return notificationDAO.getRecommendationNotifications(baseline);
    }

    public void sendNotification(User user, Resource resource, PrismNotificationTemplate notificationTemplateId, User sender, PrismAction action, Map<PrismNotificationTemplateProperty, String> extraParameters) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(user, resource, notificationTemplate, sender, action, extraParameters);
    }

    public void sendDataImportErrorNotifications(Institution institution, String errorMessage) {
        System system = systemService.getSystem();
        for (User user : userService.getUsersForResourceAndRole(institution, PrismRole.INSTITUTION_ADMINISTRATOR)) {
            NotificationTemplate template = getById(PrismNotificationTemplate.INSTITUTION_IMPORT_ERROR_NOTIFICATION);
            sendNotification(user, institution, template, system.getUser(), null, ImmutableMap.of(ERROR_MESSAGE, errorMessage));
        }
    }

    public void deleteAllNotifications() {
        entityService.deleteAll(NotificationConfiguration.class);
        entityService.deleteAll(NotificationTemplateVersion.class);
    }

    public void sendRecommendationNotification(User transientUser, LocalDate baseline) {
        User persistentUser = userService.getById(transientUser.getId());
        System system = systemService.getSystem();
        NotificationTemplate template = getById(PrismNotificationTemplate.SYSTEM_RECOMMENDATION_NOTIFICATION);
        String recommendations = advertService.getRecommendedAdvertsForEmail(persistentUser);
        sendNotification(persistentUser, system, template, system.getUser(), null, ImmutableMap.of(RECOMMENDATIONS, recommendations));
        persistentUser.getUserAccount().setLastNotifiedDateRecommendation(baseline);
    }

    public void sendRegistrationNotification(User user, ActionOutcomeDTO actionOutcome) {
        System system = systemService.getSystem();
        sendNotification(user, actionOutcome.getTransitionResource(), PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST,
                system.getUser(), actionOutcome.getTransitionAction().getId(), Collections.<PrismNotificationTemplateProperty, String>emptyMap());
    }

    public void sendResetPasswordNotification(User user, String newPassword) {
        System system = systemService.getSystem();

        sendNotification(user, systemService.getSystem(), PrismNotificationTemplate.SYSTEM_PASSWORD_NOTIFICATION,
                system.getUser(), null, ImmutableMap.of(NEW_PASSWORD_CONTROL, newPassword));
    }

    private void sendIndividualRequestNotifications(Resource resource, User invoker, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> requests = notificationDAO.getIndividualRequestNotifications(resource, invoker);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO request : requests) {
            User user = userService.getById(request.getUserId());
            NotificationTemplate notificationTemplate = getById(request.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(user, resource, notificationTemplate, invoker, request.getActionId(), Collections.<PrismNotificationTemplateProperty, String>emptyMap());
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
                sendNotification(user, resource, notificationTemplate, sender, null, Collections.<PrismNotificationTemplateProperty, String>emptyMap());
                sent.put(notificationTemplate, user);
            }
        }
    }

    private void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate, User sender, PrismAction action, Map<PrismNotificationTemplateProperty, String> extraParameters) {
        NotificationTemplateVersion templateVersion = getActiveVersion(resource, notificationTemplate);
        MailMessageDTO message = new MailMessageDTO();

        message.setTo(user);
        message.setTemplate(templateVersion);
        message.setModel(createNotificationModel(user, resource, sender, action, extraParameters));
        message.setAttachments(Lists.<AttachmentInputSource>newArrayList());

        mailSender.sendEmail(message);
    }

    private Map<PrismNotificationTemplateProperty, Object> createNotificationModel(User user, Resource resource, User sender, PrismAction action, Map<PrismNotificationTemplateProperty, String> extraParameters) {
        Map<PrismNotificationTemplateProperty, Object> model = Maps.newHashMap();
        model.put(USER, user.getDisplayName());
        model.put(USER_FIRST_NAME, user.getFirstName());
        model.put(USER_LAST_NAME, user.getLastName());
        model.put(USER_EMAIL, user.getEmail());
        model.put(USER_ACTIVATION_CODE, user.getActivationCode());
        model.put(AUTHOR, sender.getDisplayName());
        model.put(AUTHOR_EMAIL, sender.getEmail());

        model.put(DIRECTIONS_CONTROL, "to be defined");
        model.put(ACTION_CONTROL, "to be defined");
        model.put(VIEW_EDIT_CONTROL, "to be defined");

        System system = resource.getSystem();
        Institution institution = resource.getInstitution();
        Program program = resource.getProgram();
        Project project = resource.getProject();
        Application application = resource.getApplication();

        if (application != null) {
            model.put(APPLICANT, application.getUser().getDisplayName());
            model.put(APPLICATION_CODE, application.getCode());
            model.put(PROJECT_OR_PROGRAM_TITLE, project == null ? program.getTitle() : project.getTitle());
        }

        if (program != null) {
            model.put(PROGRAM_CODE, program.getCode());
            model.put(PROGRAM_TITLE, program.getTitle());
        }

        if (project != null) {
            model.put(PROJECT_CODE, project.getCode());
            model.put(PROJECT_TITLE, project.getTitle());
        }

        if (institution != null) {
            model.put(INSTITUTION_CODE, institution.getCode());
            model.put(INSTITUTION_TITLE, institution.getTitle());
        }

        for (PrismNotificationTemplateProperty parameter : extraParameters.keySet()) {
            model.put(parameter, extraParameters.get(parameter));
        }

        model.put(SYSTEM_NAME, system.getTitle());
        model.put(TIME, new Date());
        model.put(HOST, host);
        return model;
    }

    public List<PrismNotificationTemplate> getAvailableTemplates(PrismScope scope) {
        return notificationDAO.getAvailableTemplates(scope);
    }

}
