package com.zuehlke.pgadmissions.services;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinition;
import com.zuehlke.pgadmissions.mail.MailMessageDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Value("${application.host}")
    private String host;

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private UserService userService;

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
    public NotificationTemplateVersion getVersionById(Integer id) {
        return entityService.getById(NotificationTemplateVersion.class, id);
    }

    @Transactional
    public NotificationTemplateVersion getActiveVersionToEdit(Resource resource, NotificationTemplate template) {
        return notificationDAO.getActiveVersionToEdit(resource, template);
    }

    @Transactional
    public NotificationTemplateVersion getActiveVersionToSend(Resource resource, NotificationTemplate template) {
        return notificationDAO.getActiveVersionToSend(resource, template);
    }

    @Transactional
    public List<NotificationTemplateVersion> getVersions(Resource resource, NotificationTemplate template) {
        return notificationDAO.getVersions(resource, template);
    }

    @Transactional
    public List<NotificationTemplate> getTemplates() {
        return entityService.list(NotificationTemplate.class);
    }

    @Transactional
    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return notificationDAO.getConfiguration(resource, template);
    }

    @Transactional
    public List<NotificationTemplate> getActionTemplatesToManage() {
        return notificationDAO.getActiveTemplatesToManage();
    }

    @Transactional
    public void deleteObseleteNotificationConfigurations() {
        notificationDAO.deleteObseleteNotificationConfigurations(getActionTemplatesToManage());
    }

    @Transactional
    public void sendUpdateNotifications(StateAction stateAction, Resource resource, Comment comment) {
        DateTime baseline = new DateTime();
        List<UserNotificationDefinition> definitions = notificationDAO.getUpdateNotifications(stateAction, resource);

        for (UserNotificationDefinition definition : definitions) {
            UserRole userRole = entityService.getById(UserRole.class, definition.getUserRoleId());
            NotificationTemplate notificationTemplate = entityService.getByProperty(NotificationTemplate.class, "id", definition.getNotificationTemplateId());

            if (notificationTemplate.getNotificationType() == PrismNotificationType.INDIVIDUAL) {
                sendNotification(userRole.getUser(), resource, null, notificationTemplate, ImmutableMap.of("author", comment.getUser().getDisplayName()));
            } else {
                UserNotification transientUserNotification = new UserNotification().withUserRole(userRole).withNotificationTemplate(notificationTemplate)
                        .withCreatedTimestamp(baseline);
                entityService.getOrCreate(transientUserNotification);
            }
        }
    }

    @Transactional
    public void sendNotification(User user, Resource resource, Action action, NotificationTemplate notificationTemplate) {
        sendNotification(user, resource, action, notificationTemplate, Collections.<String, String>emptyMap());
    }

    @Transactional
    public void sendNotification(User user, Resource resource, Action action, NotificationTemplate notificationTemplate, Map<String, String> extraModelParams) {
        NotificationTemplateVersion templateVersion = getActiveVersionToSend(resource, notificationTemplate);
        MailMessageDTO message = new MailMessageDTO();

        message.setTo(Collections.singletonList(user));
        message.setTemplate(templateVersion);
        message.setModel(createNotificationModel(user, resource, action, templateVersion, extraModelParams));
        message.setAttachments(Lists.<PdfAttachmentInputSource>newArrayList());

        mailSender.sendEmail(message);
    }

    @Transactional
    public List<UserNotificationDefinition> getPendingUpdateNotifications() {
        return notificationDAO.getPendingUpdateNotifications();
    }

    public void sendPendingUpdateNotifications() {
        List<UserNotificationDefinition> definitions = getPendingUpdateNotifications();
        for (UserNotificationDefinition definition : definitions) {
            sendPendingNotification(definition);
        }
    }

    @Transactional
    public void sendPendingNotification(UserNotificationDefinition definition) {
        UserRole userRole = entityService.getById(UserRole.class, definition.getUserRoleId());
        NotificationTemplate template = entityService.getByProperty(NotificationTemplate.class, "id", definition.getNotificationTemplateId());

        User user = userRole.getUser();
        Resource resource = userRole.getResource();

        sendNotification(user, resource, null, template);
        deletePendingUpdateNotification(user, resource, template);
    }

    @Transactional
    public void sendDataImportErrorNotifications(Institution institution, String errorMessage) {
        for (User user : userService.getUsersForResourceAndRole(institution, PrismRole.INSTITUTION_ADMINISTRATOR)) {
            NotificationTemplate template = getById(PrismNotificationTemplate.SYSTEM_IMPORT_ERROR_NOTIFICATION);
            sendNotification(user, institution, null, template, ImmutableMap.of("message", errorMessage));
        }
    }

    @Transactional
    public void deleteAllNotifications() {
        entityService.deleteAll(NotificationConfiguration.class);
        entityService.deleteAll(NotificationTemplateVersion.class);
    }

    @Transactional
    private void deletePendingUpdateNotification(User user, Resource resource, NotificationTemplate template) {
        List<UserRole> userRoles = roleService.getUpdateNotificationRoles(user, resource, template);
        if (!userRoles.isEmpty()) {
            notificationDAO.deletePendingUpdateNotification(userRoles);
        }
    }

    @Transactional
    private Map<String, Object> createNotificationModel(User user, Resource resource, Action action, NotificationTemplateVersion notificationTemplate, Map<String, String> extraModelParams) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("user", user);
        model.put("activationCode", user.getActivationCode());
        model.put("userEmail", user.getEmail());
        model.put("userFirstName", user.getFirstName());
        model.put("userLastName", user.getLastName());

        model.put("resourceId", resource.getId().toString());
        if (action != null) {
            model.put("action", action.getId().name());
        }

        System system = resource.getSystem();
        Institution institution = resource.getInstitution();
        Program program = resource.getProgram();
        Project project = resource.getProject();
        Application application = resource.getApplication();

        if (application != null) {
            model.put("applicant", application.getUser().getDisplayName());
            model.put("applicationCode", application.getCode());
            model.put("applicationId", application.getId().toString());
        }

        if (program != null) {
            model.put("projectOrProgramTitle", project == null ? program.getTitle() : project.getTitle());
        }

        if (institution != null) {
            model.put("institutionName", institution.getName());
        }

        for (String parameter : extraModelParams.keySet()) {
            model.put(parameter, extraModelParams.get(parameter));
        }

        model.put("systemName", system.getName());
        model.put("time", new Date());
        model.put("host", host);
        return model;
    }

}
