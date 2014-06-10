package com.zuehlke.pgadmissions.services;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.NotificationTemplateDAO;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.exceptions.NotificationTemplateException;

@Service
@Transactional
public class NotificationTemplateService {

    @Autowired
    private NotificationTemplateDAO notificationTemplateDAO;
    
    @Autowired
    private EntityService entityService;

    public NotificationTemplate getById(PrismNotificationTemplate id) {
        return entityService.getByProperty(NotificationTemplate.class, "id", id);
    }

    public NotificationTemplateVersion getTemplateVersionById(Integer id) {
        return entityService.getById(NotificationTemplateVersion.class, id);
    }

    public NotificationTemplateVersion saveTemplateVersion(PrismNotificationTemplate templateId, String content, String subject) {
        NotificationTemplate notificationTemplate = getById(templateId);
        NotificationTemplateVersion templateVersion = new NotificationTemplateVersion();

        templateVersion.setNotificationTemplate(notificationTemplate);
        notificationTemplate.getVersions().add(templateVersion);

        templateVersion.setContent(content);
        templateVersion.setSubject(subject);
        templateVersion.setCreatedTimestamp(new DateTime());

        return templateVersion;
    }
    
    public void activateTemplateVersion(PrismNotificationTemplate name, Integer idToActivate) throws NotificationTemplateException {
        NotificationTemplateVersion toActivate = getTemplateVersionById(idToActivate);
        if (toActivate == null) {
            throw new NotificationTemplateException("Could not find template version with ID: \"" + idToActivate + "\"");
        }

        NotificationTemplate notificationTemplate = toActivate.getNotificationTemplate();
        notificationTemplate.setVersion(toActivate);
    }
    
    public Integer getDefaultReminderDuration(NotificationTemplate notificationTemplate) {
        return notificationTemplateDAO.getDefaultReminderDuration(notificationTemplate);
    }
}
