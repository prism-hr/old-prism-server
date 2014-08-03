package com.zuehlke.pgadmissions.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinition;
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
    public NotificationTemplateVersion getLatestVersion(Resource resource, NotificationTemplate template) {
        return notificationDAO.getLatestVersion(resource, template);
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
    public void sendUpdateNotifications(StateAction stateAction, Resource resource) {
        DateTime baseline = new DateTime();
        List<UserNotificationDefinition> definitions = notificationDAO.getUpdateNotifications(stateAction, resource);
        for (UserNotificationDefinition definition : definitions) {
            UserRole userRole = entityService.getById(UserRole.class, definition.getUserRoleId());
            NotificationTemplate notificationTemplate = entityService.getByProperty(NotificationTemplate.class, "id", definition.getNotificationTemplateId());

            if (notificationTemplate.getNotificationType() == PrismNotificationType.INDIVIDUAL) {
                sendNotification(userRole.getUser(), userRole.getResource(), notificationTemplate);
            } else {
                UserNotification transientUserNotification = new UserNotification().withUserRole(userRole).withNotificationTemplate(notificationTemplate)
                        .withCreatedTimestamp(baseline);
                entityService.getOrCreate(transientUserNotification);
            }
        }
    }

    @Transactional
    public void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate) {
        sendNotification(user, resource, notificationTemplate, Collections.<String, String> emptyMap());
    }

    @Transactional
    public void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate, Map<String, String> extraModelParams) {
        NotificationTemplateVersion templateVersion = getActiveVersionToSend(resource, notificationTemplate);
        MailMessageDTO message = new MailMessageDTO();

        message.setTo(Collections.singletonList(user));
        message.setModel(createNotificationModel(user, resource, templateVersion));
        message.setTemplate(templateVersion);
        message.setAttachments(Lists.<PdfAttachmentInputSource> newArrayList());

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
        
        sendNotification(user, resource, template);
        deletePendingUpdateNotification(user, resource, template);
    }

    @Transactional
    private void deletePendingUpdateNotification(User user, Resource resource, NotificationTemplate template) {
        List<UserRole> userRoles = roleService.getUpdateNotificationRoles(user, resource, template);
        if (!userRoles.isEmpty()) {
            notificationDAO.deletePendingUpdateNotification(userRoles);
        }
    }

    private Map<String, Object> createNotificationModel(User user, Resource resource, NotificationTemplateVersion notificationTemplate) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("user", user);

        Program program = resource.getProgram();
        Project project = resource.getProject();
        Application application = resource.getApplication();
        Institution institution = resource.getInstitution();

        if (application != null) {
            model.put("applicant", application.getUser().getDisplayName());
            model.put("applicationCode", application.getCode());
        }

        if (program != null) {
            model.put("projectOrProgramTitle", project == null ? program.getTitle() : project.getTitle());
        }

        if (institution != null) {
            model.put("institutionName", institution.getName());
        }

        model.put("systemName", resource.getSystem().getCode());

        model.put("host", host);
        return model;
    }

}
