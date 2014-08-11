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
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
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
    public List<UserNotificationDefinition> getPendingUpdateNotifications(LocalDate baseline) {
        return notificationDAO.getPendingUpdateNotifications(baseline);
    }
    
    public void sendPendingNotifications() {
        LocalDate baseline = new LocalDate();
        Resource system = systemService.getSystem();
        
        HashMultimap<PrismScope, Integer> recipients = HashMultimap.create();
        List<UserNotificationDefinition> definitions = Lists.newLinkedList();
        definitions.addAll(getPendingUpdateNotifications(baseline));
        
        for (UserNotificationDefinition definition : definitions) {
            PrismScope scopeId = definition.getNotificationTemplateId().getScope();
            Integer userId = definition.getUserId();
            
            User user = userService.getById(definition.getUserId());
            if (!recipients.get(scopeId).contains(userId) && resourceService.hasVisibleResourcesWithUpdates(scopeId.getResourceClass(), user, baseline)) {
                NotificationTemplate template = getById(definition.getNotificationTemplateId());
                sendNotification(user, system, template);
            }
            
            updateUserNotification(definition, baseline);
            recipients.put(scopeId, userId);
        }
    }

    @Transactional
    public void sendUpdateNotifications(StateAction stateAction, Resource resource, Comment comment) {
        LocalDate baseline = new LocalDate();
        List<UserNotificationDefinition> definitions = notificationDAO.getUpdateNotifications(stateAction, resource);

        for (UserNotificationDefinition definition : definitions) {
            User user = userService.getById(definition.getUserId());
            NotificationTemplate notificationTemplate = entityService.getByProperty(NotificationTemplate.class, "id", definition.getNotificationTemplateId());

            if (notificationTemplate.getNotificationType() == PrismNotificationType.INDIVIDUAL) {
                sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", comment.getUser().getDisplayName()));
            } else {
                UserNotification pending = new UserNotification().withUser(user).withNotificationTemplate(notificationTemplate).withCreatedDate(baseline);
                entityService.getOrCreate(pending);
            }
        }
    }

    @Transactional
    public void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate, Map<String, String> extraModelParams) {
        NotificationTemplateVersion templateVersion = getActiveVersionToSend(resource, notificationTemplate);
        MailMessageDTO message = new MailMessageDTO();

        message.setTo(Collections.singletonList(user));
        message.setTemplate(templateVersion);
        message.setModel(createNotificationModel(user, resource, templateVersion, extraModelParams));
        message.setAttachments(Lists.<PdfAttachmentInputSource> newArrayList());

        mailSender.sendEmail(message);
    }
    
    @Transactional
    public void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate) {
        sendNotification(user, resource, notificationTemplate, Collections.<String, String> emptyMap());
    }
    
    @Transactional
    public void sendNotification(User user, Resource resource, PrismNotificationTemplate notificationTemplateId) {
        NotificationTemplate notificationTemplate = getById(notificationTemplateId);
        sendNotification(user, resource, notificationTemplate, Collections.<String, String> emptyMap());
    }

    @Transactional
    private void updateUserNotification(UserNotificationDefinition definition, LocalDate baseline) {
        UserNotification userNotification = notificationDAO.getUserNotification(definition);
        userNotification.setCreatedDate(baseline);
    }

    @Transactional
    public void sendDataImportErrorNotifications(Institution institution, String errorMessage) {
        for (User user : userService.getUsersForResourceAndRole(institution, PrismRole.INSTITUTION_ADMINISTRATOR)) {
            NotificationTemplate template = getById(PrismNotificationTemplate.SYSTEM_IMPORT_ERROR_NOTIFICATION);
            sendNotification(user, institution, template, ImmutableMap.of("message", errorMessage));
        }
    }

    @Transactional
    public void deleteAllNotifications() {
        entityService.deleteAll(NotificationConfiguration.class);
        entityService.deleteAll(NotificationTemplateVersion.class);
    }

    @Transactional
    private Map<String, Object> createNotificationModel(User user, Resource resource, NotificationTemplateVersion notificationTemplate,
            Map<String, String> extraModelParams) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("user", user);
        model.put("userFirstName", user.getFirstName());
        model.put("userLastName", user.getLastName());

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
