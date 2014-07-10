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
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinition;
import com.zuehlke.pgadmissions.mail.MailDescriptor;
import com.zuehlke.pgadmissions.mail.MailMessageDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;

@Service
@Transactional
public class NotificationService {

    @Autowired
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

    public NotificationTemplate getById(PrismNotificationTemplate id) {
        return entityService.getByProperty(NotificationTemplate.class, "id", id);
    }

    public NotificationTemplateVersion getVersionById(Integer id) {
        return entityService.getById(NotificationTemplateVersion.class, id);
    }

    public NotificationTemplateVersion getActiveVersion(Resource resource, NotificationTemplate template) {
        return notificationDAO.getActiveVersion(resource, template);
    }

    public NotificationTemplateVersion getActiveVersion(Resource resource, PrismNotificationTemplate templateId) {
        return notificationDAO.getActiveVersion(resource, templateId);
    }

    public NotificationTemplateVersion getLatestVersion(Resource resource, NotificationTemplate template) {
        return notificationDAO.getLatestVersion(resource, template);
    }

    public List<NotificationTemplateVersion> getVersions(Resource resource, NotificationTemplate template) {
        return notificationDAO.getVersions(resource, template);
    }

    public NotificationTemplateVersion saveVersion(Resource resource, PrismNotificationTemplate templateId, String content, String subject) {
        NotificationTemplate notificationTemplate = getById(templateId);
        NotificationTemplateVersion templateVersion = new NotificationTemplateVersion();
        templateVersion.setResource(resource);
        templateVersion.setNotificationTemplate(notificationTemplate);
        templateVersion.setContent(content);
        templateVersion.setSubject(subject);
        templateVersion.setCreatedTimestamp(new DateTime());
        entityService.save(templateVersion);

        return templateVersion;
    }

    public List<NotificationTemplate> getTemplates() {
        return entityService.list(NotificationTemplate.class);
    }

    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return notificationDAO.getConfiguration(resource, template);
    }

    public List<NotificationTemplate> getActiveNotificationTemplates() {
        return notificationDAO.getActiveNotificationTemplates();
    }

    public void deleteObseleteNotificationConfigurations() {
        notificationDAO.deleteObseleteNotificationConfigurations(getActiveNotificationTemplates());
    }

    public void queueUpdateNotifications(StateAction stateAction, Resource resource) {
        DateTime individualBaseline = new DateTime();
        List<UserNotificationDefinition> definitions = notificationDAO.getUpdateNotifications(stateAction, resource);
        for (UserNotificationDefinition definition : definitions) {
            NotificationTemplate template = definition.getNotificationTemplate();
            UserNotification transientNotification = new UserNotification().withUserRole(definition.getUserRole())
                    .withNotificationTemplate(definition.getNotificationTemplate())
                    .withCreatedTimestamp(template.getNotificationType() == PrismNotificationType.INDIVIDUAL ? individualBaseline : null);
            entityService.getOrCreate(transientNotification);
        }
    }

    public void sendPendingUpdateNotifications(PrismNotificationType notificationType) {
        for (Scope scope : scopeService.getScopesAscending()) {
            for (MailDescriptor mailDescriptor : notificationDAO.getPendingUpdateNotifications(scope, notificationType)) {
                User user = mailDescriptor.getUser();
                Resource resource = mailDescriptor.getResource();
                NotificationTemplate template = mailDescriptor.getNotificationTemplate();
                
                sendNotification(user, resource, template);
                deleteSentUpdateNotifications(user, resource, template);
            }
        }
        
    }
    
    public void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate) {
        sendNotification(user, resource, notificationTemplate, Collections.<String, String> emptyMap());
    }

    public void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate, Map<String, String> extraModelParams) {
        NotificationTemplateVersion templateVersion = getActiveVersion(resource, notificationTemplate);
        MailMessageDTO message = new MailMessageDTO();

        message.setTo(Collections.singletonList(user));
        message.setModel(createNotificationModel(user, resource, templateVersion));
        message.setTemplate(templateVersion);
        message.setAttachments(Lists.<PdfAttachmentInputSource>newArrayList());

        mailSender.sendEmail(message);
    }
    
    public void sendNotification(User user, Resource resource, PrismNotificationTemplate notificationTemplateId) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(user, resource, notificationTemplate, Collections.<String, String> emptyMap());
    }
    
    public void sendNotification(User user, Resource resource, PrismNotificationTemplate notificationTemplateId, Map<String, String> extraModelParams) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(user, resource, notificationTemplate, extraModelParams);
    }
    
    private void deleteSentUpdateNotifications(User user, Resource resource, NotificationTemplate template) {
        List<UserRole> userRoles = roleService.getUpdateNotificationRoles(user, resource, template);
        if (!userRoles.isEmpty()) {
            notificationDAO.deleteSentUpdateNotifications(userRoles);
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
        }

        if (program != null) {
            model.put("projectOrProgramTitle", project != null ? project.getTitle() : program.getTitle());
        }
        
        if (institution != null) {
            model.put("institutionName", institution.getName());
        }
        

        model.put("systemName", resource.getSystem().getCode());

        model.put("host", host);
        return model;
    }
    
}
