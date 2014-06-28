package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.mail.MailSender;

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
        return entityService.getAll(NotificationTemplate.class);
    }
    
    public NotificationConfiguration getConfiguration(Resource resource, NotificationTemplate template) {
        return notificationDAO.getConfiguration(resource, template);
    }

    public List<NotificationTemplate> getConfigurableTemplates() {
        return notificationDAO.getConfigurableTemplates();
    }
    
}
