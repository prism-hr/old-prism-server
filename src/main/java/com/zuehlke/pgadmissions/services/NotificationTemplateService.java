package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.mail.MailSender;

@Service
@Transactional
public class NotificationTemplateService {
    
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

    public NotificationTemplateVersion getTemplateVersionById(Integer id) {
        return entityService.getById(NotificationTemplateVersion.class, id);
    }
    
    public NotificationTemplateVersion getActiveVersionForTemplate(PrismResource resource, NotificationTemplate template) {
        return notificationDAO.getActiveVersionForTemplate(resource, template);
    }
    
    public NotificationTemplateVersion getActiveVersionForTemplate(PrismResource resource, PrismNotificationTemplate templateId) {
        return notificationDAO.getActiveVersionForTemplate(resource, templateId);
    }
    
    public List<NotificationTemplateVersion> getVersionsForTemplate(PrismResource resource, NotificationTemplate template) {
        return notificationDAO.getVersionsForTemplate(resource, template);
    }
    
    public NotificationTemplateVersion saveTemplateVersion(PrismResource resource, PrismNotificationTemplate templateId, String content, String subject) {
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
    
    public Integer getDefaultReminderDuration(NotificationTemplate notificationTemplate) {
        return notificationDAO.getDefaultReminderDuration(notificationTemplate);
    }
    
}
