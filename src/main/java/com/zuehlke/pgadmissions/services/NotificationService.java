package com.zuehlke.pgadmissions.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.mail.MailMessageDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;

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
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ScopeService scopeService;

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

    public void deleteObseleteNotificationConfigurations() {
        notificationDAO.deleteObseleteNotificationConfigurations(getWorkflowTemplates());
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
                sendNotification(user, resource, notificationTemplate.getReminderTemplate(), ImmutableMap.of("author", invoker.getDisplayName()));
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
                sendNotification(user, resource, sendTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
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
                sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
                user.setLastNotifiedDate(resource.getClass(), baseline);
                sent.put(notificationTemplate, user);
            }
        }
    }

    public List<User> getRecommendationNotifications(LocalDate baseline) {
        return notificationDAO.getRecommendationNotifications(baseline);
    }

    public void sendNotification(User user, Resource resource, PrismNotificationTemplate notificationTemplateId, Map<String, String> extraParameters) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(user, resource, notificationTemplate, extraParameters);
    }

    public void sendNotification(User user, Resource resource, PrismNotificationTemplate notificationTemplateId) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(user, resource, notificationTemplate, Collections.<String, String> emptyMap());
    }

    public void sendDataImportErrorNotifications(Institution institution, String errorMessage) {
        for (User user : userService.getUsersForResourceAndRole(institution, PrismRole.INSTITUTION_ADMINISTRATOR)) {
            NotificationTemplate template = getById(PrismNotificationTemplate.INSTITUTION_IMPORT_ERROR_NOTIFICATION);
            sendNotification(user, institution, template, ImmutableMap.of("message", errorMessage));
        }
    }

    public void deleteAllNotifications() {
        entityService.deleteAll(NotificationConfiguration.class);
        entityService.deleteAll(NotificationTemplateVersion.class);
    }

    public void sendRecommendationNotification(User user, LocalDate baseline) {
        System system = systemService.getSystem();
        NotificationTemplate template = getById(PrismNotificationTemplate.SYSTEM_RECOMMENDATION_NOTIFICATION);
        String recommendations = advertService.getRecommendedAdvertsForEmail(user);
        sendNotification(user, system, template, ImmutableMap.of("author", system.getUser().getDisplayName(), "recommendations", recommendations));
        user.getUserAccount().setLastNotifiedDateRecommendation(baseline);
    }

    private void sendIndividualRequestNotifications(Resource resource, User invoker, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> requests = notificationDAO.getIndividualRequestNotifications(resource, invoker);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO request : requests) {
            User user = userService.getById(request.getUserId());
            NotificationTemplate notificationTemplate = getById(request.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
                sent.put(notificationTemplate, user);
            }

            Role role = roleService.getById(request.getRoleId());
            UserRole userRole = roleService.getUserRole(resource, user, role);
            userRole.setNotificationTemplate(notificationTemplate);
            userRole.setLastNotifiedDate(baseline);
        }
    }

    private void sendIndividualUpdateNotifications(Resource resource, Action action, User invoker, LocalDate baseline) {
        State state = resource.getPreviousState();

        List<UserNotificationDefinitionDTO> updates = notificationDAO.getIndividualUpdateNotifications(resource, state, action, invoker, baseline);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO update : updates) {
            User user = userService.getById(update.getUserId());
            NotificationTemplate notificationTemplate = getById(update.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
                sent.put(notificationTemplate, user);
            }
        }
    }

    private void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate, Map<String, String> extraParameters) {
        NotificationTemplateVersion templateVersion = getActiveVersion(resource, notificationTemplate);
        MailMessageDTO message = new MailMessageDTO();

        message.setTo(user);
        message.setTemplate(templateVersion);
        message.setModel(createNotificationModel(user, resource, templateVersion, extraParameters));
        message.setAttachments(Lists.<PdfAttachmentInputSource> newArrayList());

        mailSender.sendEmail(message);
    }

    private Map<String, Object> createNotificationModel(User user, Resource resource, NotificationTemplateVersion notificationTemplate,
            Map<String, String> extraParameters) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("user", user.getDisplayName());
        model.put("userFirstName", user.getFirstName());
        model.put("userLastName", user.getLastName());
        model.put("userEmail", user.getEmail());
        model.put("activationCode", user.getActivationCode());

        model.put("resourceId", resource.getId().toString());
        model.put("actionUrl", "to be defined");

        System system = resource.getSystem();
        Institution institution = resource.getInstitution();
        Program program = resource.getProgram();
        Project project = resource.getProject();
        Application application = resource.getApplication();

        if (application != null) {
            model.put("applicant", application.getUser().getDisplayName());
            model.put("applicationId", application.getId().toString());
            model.put("applicationCode", application.getCode());
            model.put("projectOrProgramTitle", project == null ? program.getTitle() : project.getTitle());
        }

        if (program != null) {
            model.put("programId", program.getId().toString());
            model.put("programCode", program.getCode());
            model.put("programTitle", program.getTitle());
        }

        if (project != null) {
            model.put("projectId", project.getId().toString());
            model.put("projectCode", project.getCode());
            model.put("projectTitle", project.getTitle());
        }

        if (institution != null) {
            model.put("institutionId", institution.getId().toString());
            model.put("institutionCode", institution.getCode());
            model.put("institutionTitle", institution.getTitle());
        }

        for (String parameter : extraParameters.keySet()) {
            model.put(parameter, extraParameters.get(parameter));
        }

        model.put("systemName", system.getName());
        model.put("time", new Date());
        model.put("host", host);
        return model;
    }

}
