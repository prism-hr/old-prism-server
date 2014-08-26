package com.zuehlke.pgadmissions.services;

import com.google.common.collect.*;
import com.zuehlke.pgadmissions.dao.NotificationDAO;
import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationPurpose;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.dto.ResourceActionDTO;
import com.zuehlke.pgadmissions.dto.StateChangeDTO;
import com.zuehlke.pgadmissions.dto.UserNotificationDefinition;
import com.zuehlke.pgadmissions.mail.MailMessageDTO;
import com.zuehlke.pgadmissions.mail.MailSender;
import com.zuehlke.pgadmissions.pdf.PdfAttachmentInputSource;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class NotificationService {

    @Value("${application.host}")
    private String host;

    @Autowired
    private NotificationDAO notificationDAO;

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
        return getConfiguration(resource, template).getReminderInterval();
    }

    @Transactional
    public List<NotificationTemplate> getTemplates() {
        return entityService.list(NotificationTemplate.class);
    }

    // FIXME: scoping for those that can be configured in a given context.
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
        LocalDate baseline = new LocalDate();

        HashMultimap<NotificationTemplate, User> sentIndividual = HashMultimap.create();
        HashMultimap<Scope, User> sentSyndicated = HashMultimap.create();

        for (Scope scope : scopeService.getScopesAscending()) {
            List<UserNotificationDefinition> definitions = getDeferredWorkflowNotifications(scope, baseline);

            for (UserNotificationDefinition definition : definitions) {
                sendDeferredWorkflowNotification(scope, definition, sentIndividual, sentSyndicated, baseline);
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
        sendNotification(user, resource, notificationTemplate, Collections.<String, String>emptyMap());
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
    private void createOrUpdateUserNotification(Resource resource, UserRole userRole, NotificationTemplate notificationTemplate, LocalDate baseline) {
        UserNotification transientUserNotification = new UserNotification().withResource(resource).withUserRole(userRole)
                .withNotificationTemplate(notificationTemplate).withCreatedDate(baseline);
        entityService.createOrUpdate(transientUserNotification);
    }

    @Transactional
    private List<UserNotificationDefinition> getWorkflowNotifications(Resource resource, Action action, User invoker) {
        LocalDate baseline = new LocalDate();
        List<UserNotificationDefinition> definitions = Lists.newLinkedList();
        definitions.addAll(notificationDAO.getRequestNotifications(resource, action, invoker));
        definitions.addAll(notificationDAO.getUpdateNotifications(resource, action, invoker, baseline));
        return definitions;
    }

    @Transactional
    private List<UserNotificationDefinition> getDeferredWorkflowNotifications(Scope scope, LocalDate baseline) {
        User invoker = systemService.getSystem().getUser();
        List<UserNotificationDefinition> definitions = Lists.newLinkedList();
        definitions.addAll(getDeferredRequestNotifications(scope, invoker));
        definitions.addAll(getDeferredUpdateNotifications(scope, invoker, baseline));
        return definitions;
    }

    @Transactional
    private List<UserNotificationDefinition> getDeferredRequestNotifications(Scope scope, User invoker) {
        List<ResourceActionDTO> resourceActions = resourceService.getResoucesFlaggedAsUrgent(scope);
        Set<UserNotificationDefinition> definitions = Sets.newLinkedHashSet();

        for (ResourceActionDTO resourceAction : resourceActions) {
            Action action = resourceAction.getAction();
            Resource resource = resourceService.getById(action.getScope().getId().getResourceClass(), resourceAction.getResourceId());
            definitions.addAll(notificationDAO.getDeferredRequestNotifications(resource, action, invoker));
        }

        return Lists.newLinkedList(definitions);
    }

    @Transactional
    private List<UserNotificationDefinition> getDeferredUpdateNotifications(Scope scope, User invoker, LocalDate baseline) {
        List<StateChangeDTO> updates = resourceService.getRecentStateChanges(scope, baseline);
        Set<UserNotificationDefinition> definitions = Sets.newLinkedHashSet();

        for (StateChangeDTO update : updates) {
            definitions.addAll(notificationDAO.getDeferredUpdateNotifications(update.getResource(), update.getAction(), invoker, baseline.minusDays(1)));
        }

        return Lists.newLinkedList(definitions);
    }

    @Transactional
    private void sendDeferredWorkflowNotification(Scope scope, UserNotificationDefinition definition, HashMultimap<NotificationTemplate, User> sentIndividual,
                                                  HashMultimap<Scope, User> sentSyndicated, LocalDate baseline) {
        UserRole userRole = roleService.getUserRoleById(definition.getUserRoleId());
        User user = userRole.getUser();

        NotificationTemplate notificationTemplate = getById(definition.getNotificationTemplateId());
        PrismNotificationType notificationType = notificationTemplate.getNotificationType();

        Resource taskResource = resourceService.getById(notificationTemplate.getScope().getId().getResourceClass(), definition.getResourceId());
        Resource messageResource = taskResource.getEnclosingResource(notificationTemplate.getScope().getId());

        if (notificationType == PrismNotificationType.INDIVIDUAL && !sentIndividual.get(notificationTemplate).contains(user)) {
            sendDeferredWorkflowRequestOrReminder(messageResource, user, userRole, notificationTemplate, baseline);
            sentIndividual.put(notificationTemplate, user);
        } else if (notificationType == PrismNotificationType.SYNDICATED && !sentSyndicated.get(scope).contains(user)) {
            if (notificationTemplate.getNotificationPurpose() == PrismNotificationPurpose.REQUEST) {
                sendNotification(user, messageResource, notificationTemplate, ImmutableMap.of("author", messageResource.getUser().getDisplayName()));
            } else {
                sendDeferredWorkflowRequestOrReminder(messageResource, user, userRole, notificationTemplate, baseline);
            }
            sentSyndicated.put(scope, user);
        }
    }

    @Transactional
    private void sendDeferredWorkflowRequestOrReminder(Resource resource, User user, UserRole userRole, NotificationTemplate template, LocalDate baseline) {
        UserNotification userNotification = notificationDAO.getUserNotification(resource, userRole, template);
        if (userNotification == null) {
            sendNotification(user, resource, template, ImmutableMap.of("author", resource.getUser().getDisplayName()));
        } else {
            Integer reminderInterval = getReminderInterval(resource, template);
            if (baseline.minusDays(1) == userNotification.getCreatedDate().plusDays(reminderInterval)) {
                sendNotification(user, resource, template.getReminderTemplate(), ImmutableMap.of("author", resource.getUser().getDisplayName()));
            }
        }
        createOrUpdateUserNotification(resource, userRole, template, baseline.minusDays(1));
    }

    @Transactional
    private void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate, Map<String, String> extraParameters) {
        NotificationTemplateVersion templateVersion = getActiveVersion(resource, notificationTemplate);
        MailMessageDTO message = new MailMessageDTO();

        message.setTo(Collections.singletonList(user));
        message.setTemplate(templateVersion);
        message.setModel(createNotificationModel(user, resource, templateVersion, extraParameters));
        message.setAttachments(Lists.<PdfAttachmentInputSource>newArrayList());

        mailSender.sendEmail(message);
    }

    @Transactional
    private void sendNotification(User user, Resource resource, NotificationTemplate notificationTemplate) {
        sendNotification(user, resource, notificationTemplate, Collections.<String, String>emptyMap());
    }

    @Transactional
    private Map<String, Object> createNotificationModel(User user, Resource resource, NotificationTemplateVersion notificationTemplate,
                                                        Map<String, String> extraParameters) {
        Map<String, Object> model = Maps.newHashMap();
        model.put("user", user.getDisplayName());
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
            model.put("applicationId", application.getId().toString());
            model.put("applicationCode", application.getCode());
            model.put("projectOrProgramTitle", project == null ? program.getTitle() : project.getTitle());
        }

        if (program != null) {
            model.put("programId", program.getId().toString());
            model.put("programCode", program.getCode());
            model.put("programTitle", program.getTitle());
        }

        if(project != null) {
            model.put("projectId", project.getId().toString());
            model.put("projectCode", project.getCode());
            model.put("projectTitle", project.getTitle());
        }

        if (institution != null) {
            model.put("institutionId", institution.getId().toString());
            model.put("institutionCode", institution.getCode());
            model.put("institutionTitle", institution.getTitle());
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
