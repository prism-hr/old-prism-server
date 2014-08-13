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
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserNotification;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationPurpose;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
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

    public void sendDeferredWorkflowNotifications() {
        LocalDate baseline = new LocalDate();
        Resource system = systemService.getSystem();
        HashMultimap<Scope, User> syndicatedSent = HashMultimap.create();

        for (Scope scope : scopeService.getScopesAscending()) {
            for (UserNotificationDefinition definition : getDeferredNotifications(scope, baseline)) {
                Resource resource = resourceService.getById(scope.getId().getResourceClass(), definition.getResourceId());
                
                UserRole userRole = roleService.getUserRoleById(definition.getUserRoleId());
                User user = userRole.getUser();
                
                NotificationTemplate notificationTemplate = getById(definition.getNotificationTemplateId());
                
                if (notificationTemplate.getNotificationType() == PrismNotificationType.INDIVIDUAL
                        || !syndicatedSent.get(notificationTemplate.getScope()).contains(user)) {
                    User invoker = resource.getUser();
                    
                    sendNotification(user, resource == null ? system : resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
                    
                    if (notificationTemplate.getNotificationType() == PrismNotificationType.SYNDICATED) {
                        syndicatedSent.put(scope, user);
                    }
                }

                if (notificationTemplate.getNotificationPurpose() == PrismNotificationPurpose.REQUEST) {
                    createOrUpdateUserNotification(resource, userRole, notificationTemplate, baseline);
                }
            }
        }
    }

    @Transactional
    public void sendWorkflowNotifications(Resource resource, Action action, User invoker) {
        LocalDate baseline = new LocalDate();
        List<UserNotificationDefinition> definitions = getWorkflowNotifications(resource, action, invoker);
        HashMultimap<NotificationTemplate, User> sent = HashMultimap.create();

        for (UserNotificationDefinition definition : definitions) {
            UserRole userRole = roleService.getUserRoleById(definition.getUserRoleId());
            User user = userRole.getUser();
            
            NotificationTemplate notificationTemplate = getById(definition.getNotificationTemplateId());

            if (!sent.get(notificationTemplate).contains(user)) {
                sendNotification(user, resource, notificationTemplate, ImmutableMap.of("author", invoker.getDisplayName()));
                sent.put(notificationTemplate, user);
            }

            if (notificationTemplate.getNotificationPurpose() == PrismNotificationPurpose.REQUEST) {
                createOrUpdateUserNotification(resource, userRole, notificationTemplate, baseline);
            }
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
    public void deleteUserNotification(UserRole roleToRemove) {
        notificationDAO.deleteUserNotification(roleToRemove);
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

    @Transactional
    private void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate, Map<String, String> extraParameters) {
        NotificationTemplateVersion templateVersion = getActiveVersionToSend(resource, notificationTemplate);
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

        for (String parameter : extraParameters.keySet()) {
            model.put(parameter, extraParameters.get(parameter));
        }

        model.put("systemName", system.getName());
        model.put("time", new Date());
        model.put("host", host);
        return model;
    }

    @Transactional
    private void createOrUpdateUserNotification(Resource resource, UserRole userRole, NotificationTemplate notificationTemplate, LocalDate baseline) {
        UserNotification transientUserNotification = new UserNotification().withResource(resource).withUserRole(userRole)
                .withNotificationTemplate(notificationTemplate).withCreatedDate(baseline);
        entityService.createOrUpdate(transientUserNotification);
    }

    @Transactional
    private List<UserNotificationDefinition> getWorkflowNotifications(Resource resource, Action action, User invoker) {
        List<UserNotificationDefinition> definitions = Lists.newLinkedList();
        definitions.addAll(notificationDAO.getRequestNotifications(resource, action, invoker));
        definitions.addAll(notificationDAO.getUpdateNotifications(resource, action, invoker));
        return definitions;
    }

    @Transactional
    public List<UserNotificationDefinition> getDeferredNotifications(Scope scope, LocalDate baseline) {
        return notificationDAO.getDeferredNotifications(scope, baseline);
    }

}
