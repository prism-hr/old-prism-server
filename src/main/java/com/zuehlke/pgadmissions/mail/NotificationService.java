package com.zuehlke.pgadmissions.mail;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.services.NotificationTemplateService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class NotificationService {

    @Value("${application.host}")
    private String host;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private NotificationTemplateService notificationTemplateService;

    @Autowired
    private MailSender mailSender;
    
    public void sendEmailNotification(User recipient, PrismResource resource, PrismNotificationTemplate templateName) {
        sendEmailNotification(recipient, resource, templateName, null);
    }
    
    public void sendEmailNotification(User recipient, PrismResource resource, PrismNotificationTemplate templateName, Comment comment) {
        sendEmailNotification(recipient, resource, templateName, comment, Collections.<String, String> emptyMap());
    }

    public void sendEmailNotification(User recipient, PrismResource resource, PrismNotificationTemplate templateId, Comment comment,
            Map<String, String> extraModelParams) {
        NotificationTemplateVersion notificationTemplate = notificationTemplateService.getActiveVersionForTemplate(resource, templateId);
        PrismEmailMessage message = new PrismEmailMessage();

        message.setTo(Collections.singletonList(recipient));
        message.setModel(createModel(recipient, resource, notificationTemplate, comment));
        message.setTemplate(notificationTemplate);

        mailSender.sendEmail(message);
    }
    
    private Map<String, Object> createModel(User recipient, PrismResource resource, NotificationTemplateVersion notificationTemplate, Comment comment) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("recipient", recipient.getDisplayName());

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
        

        model.put("systemName", resource.getSystem().getName());

        model.put("host", host);
        return model;
    }

}
