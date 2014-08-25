package com.zuehlke.pgadmissions.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
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
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationPurpose;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinitionDTO;
import com.zuehlke.pgadmissions.mail.MailMessageDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;

@Service
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

    @Transactional
    public NotificationTemplate getById(PrismNotificationTemplate id) {
        return entityService.getByProperty(NotificationTemplate.class, "id", id);
    }

    @Transactional
    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return notificationDAO.getConfiguration(resource, template);
    }

    @Transactional
    public NotificationTemplateVersion getActiveVersion(Resource resource, NotificationTemplate template) {
        NotificationConfiguration configuration = notificationDAO.getConfiguration(resource, template);
        return configuration == null ? null : configuration.getNotificationTemplateVersion();
    }

    @Transactional
    public Integer getReminderInterval(Resource resource, NotificationTemplate template) {
        NotificationConfiguration configuration = getConfiguration(resource, template);
        return configuration == null ? 1 : configuration.getReminderInterval();
    }

    @Transactional
    public List<NotificationTemplate> getTemplates() {
        return entityService.list(NotificationTemplate.class);
    }

    @Transactional
    public List<NotificationTemplate> getWorkflowTemplates() {
        List<NotificationTemplate> templates = Lists.newLinkedList();
        templates.addAll(notificationDAO.getWorkflowRequestTemplates());
        templates.addAll(notificationDAO.getWorkflowUpdateTemplates());
        return templates;
    }

    @Transactional
    public void deleteObseleteNotificationConfigurations() {
        notificationDAO.deleteObseleteNotificationConfigurations(getWorkflowTemplates());
    }

    public void sendDeferredWorkflowNotifications() {
        User invoker = systemService.getSystem().getUser();
        LocalDate baseline = new LocalDate();

        for (Scope scope : scopeService.getScopesAscending()) {
            sendRequestReminders(scope, invoker, baseline);
            sendSyndicatedWorkflowNotifications(scope, invoker, baseline);
        }
    }

    @Transactional
    public void sendWorkflowNotifications(Resource resource, Comment comment) {
        User invoker = comment.getAuthor();
        LocalDate baseline = new LocalDate();

        List<UserNotificationDefinitionDTO> definitions = Lists.newLinkedList();
        definitions.addAll(notificationDAO.getRequestNotifications(resource, invoker));
        definitions.addAll(notificationDAO.getUpdateNotifications(resource, comment.getAction(), invoker));

        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO definition : definitions) {
            User user = userService.getById(definition.getUserId());

            NotificationTemplate notificationTemplate = getById(definition.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
                sent.put(notificationTemplate, user);
            }

            if (notificationTemplate.getNotificationPurpose() == PrismNotificationPurpose.REQUEST) {
                Role role = roleService.getById(definition.getRoleId());
                UserRole userRole = roleService.getUserRole(resource, user, role);
                userRole.setNotificationTemplate(notificationTemplate);
                userRole.setNotificationLastSentDate(baseline);
            }
        }
    }

    public void sendRecommendationNotifications() {
        LocalDate baseline = new LocalDate();
        List<User> users = notificationDAO.getRecommendationNotifications(baseline);

        System system = systemService.getSystem();
        NotificationTemplate template = getById(PrismNotificationTemplate.SYSTEM_RECOMMENDATION_NOTIFICATION);

        for (User user : users) {
            sendRecommendationNotification(system, user, template, baseline);
        }
    }

    @Transactional
    public void sendNotification(User user, Resource resource, PrismNotificationTemplate notificationTemplateId, Map<String, String> extraParameters) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(user, resource, notificationTemplate, extraParameters);
    }

    @Transactional
    public void sendNotification(User user, Resource resource, PrismNotificationTemplate notificationTemplateId) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(user, resource, notificationTemplate, Collections.<String, String> emptyMap());
    }

    @Transactional
    public void sendDataImportErrorNotifications(Institution institution, String errorMessage) {
        for (User user : userService.getUsersForResourceAndRole(institution, PrismRole.INSTITUTION_ADMINISTRATOR)) {
            NotificationTemplate template = getById(PrismNotificationTemplate.INSTITUTION_IMPORT_ERROR_NOTIFICATION);
            sendNotification(user, institution, template, ImmutableMap.of("message", errorMessage));
        }
    }

    @Transactional
    public void deleteAllNotifications() {
        entityService.deleteAll(NotificationConfiguration.class);
        entityService.deleteAll(NotificationTemplateVersion.class);
    }

    private void sendRequestReminders(Scope scope, User invoker, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> definitions = notificationDAO.getRequestReminders(scope, baseline);
        HashMultimap<String, User> sent = HashMultimap.create();

        for (UserNotificationDefinitionDTO definition : definitions) {
            sendRequestReminder(definition, invoker, baseline, sent);
        }
    }

    private void sendSyndicatedWorkflowNotifications(Scope scope, User invoker, LocalDate baseline) {
        List<UserNotificationDefinitionDTO> definitions = Lists.newLinkedList();
        definitions.addAll(notificationDAO.getSyndicatedRequestNotifications(scope, baseline));
        definitions.addAll(notificationDAO.getSyndicatedUpdateNotifications(scope, baseline));

        Set<User> sent = Sets.newHashSet();

        for (UserNotificationDefinitionDTO definition : definitions) {
            sendSyndicatedWorkflowNotification(definition, invoker, baseline, sent);
        }

    }

    @Transactional
    private void sendRequestReminder(UserNotificationDefinitionDTO definition, User invoker, LocalDate baseline, HashMultimap<String, User> sent) {
        User user = userService.getById(definition.getUserId());

        PrismNotificationTemplate notificationTemplateId = definition.getNotificationTemplateId();
        Resource resource = resourceService.getById(notificationTemplateId.getScope().getResourceClass(), definition.getResourceId());
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);

        Integer reminderInterval = getReminderInterval(resource, notificationTemplate) + 1;
        String messageKey = resource.getId().toString() + notificationTemplateId.name();

        if (!sent.get(messageKey).contains(user) && definition.getLastSentDate().plusDays(reminderInterval) == baseline) {
            sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
            sent.put(messageKey, user);
        }

        Role role = roleService.getById(definition.getRoleId());
        UserRole userRole = roleService.getUserRole(resource, user, role);
        userRole.setNotificationLastSentDate(baseline);
    }

    @Transactional
    private void sendSyndicatedWorkflowNotification(UserNotificationDefinitionDTO definition, User invoker, LocalDate baseline, Set<User> sent) {
        User user = userService.getById(definition.getUserId());

        NotificationTemplate notificationTemplate = getById(definition.getNotificationTemplateId());
        Resource resource = systemService.getSystem();

        if (!sent.contains(user)) {
            if (notificationTemplate.getNotificationPurpose() == PrismNotificationPurpose.REQUEST) {
                if (definition.getLastSentDate() == null) {
                    sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
                    sent.add(user);
                } else {
                    Integer reminderInterval = getReminderInterval(resource, notificationTemplate) + 1;
                    if (definition.getLastSentDate().plusDays(reminderInterval) == baseline) {
                        sendNotification(user, resource, notificationTemplate.getReminderTemplate(), ImmutableMap.of("author", invoker.getDisplayName()));
                        sent.add(user);
                    }
                }
            } else {
                sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
                sent.add(user);
            }

            createOrUpdateUserNotification(user, notificationTemplate, baseline);
        }

    }

    @Transactional
    private void sendRecommendationNotification(System system, User user, NotificationTemplate template, LocalDate baseline) {
        String recommendations = advertService.getRecommendedAdvertsForEmail(user);
        sendNotification(user, system, template, ImmutableMap.of("author", system.getUser().getDisplayName(), "recommendations", recommendations));
        createOrUpdateUserNotification(user, template, baseline);
    }

    @Transactional
    private void createOrUpdateUserNotification(User user, NotificationTemplate notificationTemplate, LocalDate baseline) {
        UserNotification transientUserNotification = new UserNotification().withUser(user).withNotificationTemplate(notificationTemplate)
                .withLastSentDate(baseline);
        entityService.createOrUpdate(transientUserNotification);
    }

    @Transactional
    private void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate, Map<String, String> extraParameters) {
        NotificationTemplateVersion templateVersion = getActiveVersion(resource, notificationTemplate);
        MailMessageDTO message = new MailMessageDTO();

        message.setTo(Collections.singletonList(user));
        message.setTemplate(templateVersion);
        message.setModel(createNotificationModel(user, resource, templateVersion, extraParameters));
        message.setAttachments(Lists.<PdfAttachmentInputSource> newArrayList());

        mailSender.sendEmail(message);
    }

    @Transactional
    private void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate) {
        sendNotification(user, resource, notificationTemplate, Collections.<String, String> emptyMap());
    }

    @Transactional
    private Map<String, Object> createNotificationModel(User user, Resource resource, NotificationTemplateVersion notificationTemplate,
            Map<String, String> extraParameters) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("user", user);
        model.put("userFirstName", user.getFirstName());
        model.put("userLastName", user.getLastName());
        model.put("userEmail", user.getEmail());
        model.put("activationCode", user.getActivationCode());

        model.put("resourceId", resource.getId().toString());

        System system = resource.getSystem();
        Institution institution = resource.getInstitution();
        Program program = resource.getProgram();
        Project project = resource.getProject();
        Application application = resource.getApplication();

        if (application != null) {
            model.put("applicant", application.getUser().getDisplayName());
            model.put("applicationCode", application.getCode());
        }

        if (program != null) {
            model.put("projectOrProgramTitle", project == null ? program.getTitle() : project.getTitle());
        }

        if (institution != null) {
            model.put("institutionName", institution.getTitle());
            model.put("institutionCode", institution.getCode());
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
