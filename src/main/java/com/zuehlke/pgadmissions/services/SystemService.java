package com.zuehlke.pgadmissions.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Configuration;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.mail.NotificationService;

@Service
@Transactional
public class SystemService {

    private final String EMAIL_DEFAULT_SUBJECT_DIRECTORY = "/email/subject";
    
    private final String EMAIL_DEFAULT_CONTENT_DIRECTORY = "/email/content/";
    
    @Autowired
    private Environment environment;

    @Autowired
    private EntityService entityService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationTemplateService notificationTemplateService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    public System getSystem() {
        return (System) entityService.getByProperty(com.zuehlke.pgadmissions.domain.System.class, "name", "PRiSM");
    }

    public System getOrCreateSystem(User systemUser) {
        String systemName = environment.getProperty("system.name");
        System transientSystem = new System().withName(systemName).withUser(systemUser);
        return (System) entityService.getOrCreate(transientSystem);
    }

    public Scope getSystemScope(PrismScope scopeId) {
        return entityService.getByProperty(Scope.class, "id", scopeId);
    }

    public List<Scope> getAllScopes() {
        return entityService.getAll(Scope.class);
    }

    public void initialiseSystem() {
        String userFirstName = environment.getProperty("system.user.firstName");
        String userLastName = environment.getProperty("system.user.lastName");
        String userEmail = environment.getProperty("system.user.email");

        User systemUser = userService.getOrCreateUser(userFirstName, userLastName, userEmail);
        System system = getOrCreateSystem(systemUser);

        initialiseDefinitions(system);

        Role systemRole = roleService.getById(PrismRole.SYSTEM_ADMINISTRATOR);
        roleService.getOrCreateUserRole(system, systemUser, systemRole);

        if (systemUser.getUserAccount() == null) {
            notificationService.sendEmailNotification(systemUser, system, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        }

    }

    private void initialiseDefinitions(System system) {
        initialiseScopes();
        initialiseActions();
        initialiseConfigurations(system);
        initialiseNotificationTemplates(system);
    }

    private void initialiseScopes() {
        for (PrismScope prismScope : PrismScope.values()) {
            Scope scope = new Scope().withId(prismScope).withPrecedence(prismScope.getPrecedence());
            entityService.save(scope);
        }
    }

    private void initialiseActions() {
        for (PrismAction prismAction : PrismAction.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismAction.getScope());
            Action action = new Action().withId(prismAction).withActionType(prismAction.getActionType()).withScope(scope);
            entityService.save(action);
        }
    }

    private void initialiseConfigurations(System system) {
        for (PrismConfiguration prismConfiguration : PrismConfiguration.values()) {
            Configuration configuration = new Configuration().withSystem(system).withValue(prismConfiguration.getDefaultValue());
            entityService.save(configuration);
        }
    }

    private void initialiseNotificationTemplates(System system) {
        List<NotificationTemplate> templatesWithReminders = Lists.newArrayList();
        for (PrismNotificationTemplate prismTemplate : PrismNotificationTemplate.values()) {
            NotificationTemplate template = new NotificationTemplate().withId(prismTemplate).withType(prismTemplate.getNotificationType());
            entityService.save(template);
            String defaultSubject = getFileContent(EMAIL_DEFAULT_SUBJECT_DIRECTORY + prismTemplate.getInitialTemplateSubject());
            String defaultContent = getFileContent(EMAIL_DEFAULT_CONTENT_DIRECTORY + prismTemplate.getInitialTemplateContent());
            NotificationTemplateVersion version = new NotificationTemplateVersion().withNotificationTemplate(template).withSubject(defaultSubject).withContent(defaultContent)
                    .withCreatedTimestamp(new DateTime());
            entityService.save(version);
        }
        for (NotificationTemplate template : templatesWithReminders) {
            PrismNotificationTemplate reminder = PrismNotificationTemplate.getReminderTemplate(template.getId());
            NotificationTemplate reminderTemplate = notificationTemplateService.getById(reminder);
            Integer reminderInterval = PrismNotificationTemplate.getReminderInterval(reminder);
            template.setReminderTemplate(reminderTemplate);
            NotificationConfiguration configuration = new NotificationConfiguration().withSystem(system).withReminderInterval(reminderInterval);
            entityService.save(configuration);
        }
    }
    
    private String getFileContent(String filePath) {
        String line;
        StringBuilder output = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String lineBreak = java.lang.System.getProperty(java.lang.System.lineSeparator());
            while((line = reader.readLine()) != null) {
                output.append(line);
                output.append(lineBreak);
            }
            reader.close();
            return output.toString();

        } catch (IOException e) {
            throw new Error("Could not access default notification template", e);
        }
    }

}
