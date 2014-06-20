package com.zuehlke.pgadmissions.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Configuration;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateDuration;
import com.zuehlke.pgadmissions.domain.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismConfiguration;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.PrismStateTransitionEvaluation;
import com.zuehlke.pgadmissions.mail.MailService;

@Service
@Transactional
public class SystemService {

    private final String EMAIL_DEFAULT_SUBJECT_DIRECTORY = "/email/subject";

    private final String EMAIL_DEFAULT_CONTENT_DIRECTORY = "/email/content/";

    @Autowired
    private Environment environment;

    @Autowired
    private ConfigurationService configurationService;
    
    @Autowired
    private EntityService entityService;

    @Autowired
    private MailService mailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private StateService stateService;
    
    @Autowired
    private UserService userService;

    public System getSystem() {
        String systemName = environment.getProperty("system.name");
        return (System) entityService.getByProperty(com.zuehlke.pgadmissions.domain.System.class, "name", systemName);
    }

    public System getOrCreateSystem(User systemUser) {
        String systemName = environment.getProperty("system.name");
        System transientSystem = new System().withName(systemName).withUser(systemUser);
        return (System) entityService.getOrCreate(transientSystem);
    }

    public Scope getScope(PrismScope scopeId) {
        return entityService.getByProperty(Scope.class, "id", scopeId);
    }
    
    public Integer getStateDuration(State state) {
        return stateService.getStateDuration(getSystem(), state);
    }
    
    public Integer getReminderDuration(NotificationTemplate template) {
        return notificationService.getReminderDuration(getSystem(), template);
    }
 
    public List<Scope> getScopes() {
        return entityService.getAll(Scope.class);
    }
    
    public List<Configuration> getConfigurations() {
        return configurationService.getConfigurations(getSystem());
    }

    public void initialiseSystem() {
        String userFirstName = environment.getProperty("system.user.firstName");
        String userLastName = environment.getProperty("system.user.lastName");
        String userEmail = environment.getProperty("system.user.email");

        User systemUser = userService.getOrCreateUser(userFirstName, userLastName, userEmail);
        System system = getOrCreateSystem(systemUser);

        initialiseScopes();
        initialiseActions();
        initialiseConfigurations(system);
        initialiseNotificationTemplates(system);
        initialiseRoles();
        initialiseStates(system);
        initialiseStateTransitionEvaluations();
        
        State systemRunning = stateService.getById(PrismState.SYSTEM_APPROVED);
        system.setState(systemRunning);

        Role systemRole = roleService.getById(PrismRole.SYSTEM_ADMINISTRATOR);
        roleService.getOrCreateUserRole(system, systemUser, systemRole);

        if (systemUser.getUserAccount() == null) {
            mailService.sendEmailNotification(systemUser, system, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        }

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
            Scope scope = entityService.getByProperty(Scope.class, "id", prismTemplate.getScope());
            NotificationTemplate template = new NotificationTemplate().withId(prismTemplate).withNotificationType(prismTemplate.getNotificationType())
                    .withNotificationPurpose(prismTemplate.getNotificationPurpose()).withScope(scope);
            entityService.save(template);
            String defaultSubject = getFileContent(EMAIL_DEFAULT_SUBJECT_DIRECTORY + prismTemplate.getInitialTemplateSubject());
            String defaultContent = getFileContent(EMAIL_DEFAULT_CONTENT_DIRECTORY + prismTemplate.getInitialTemplateContent());
            NotificationTemplateVersion version = new NotificationTemplateVersion().withNotificationTemplate(template).withSubject(defaultSubject)
                    .withContent(defaultContent).withCreatedTimestamp(new DateTime());
            entityService.save(version);
            if (PrismNotificationTemplate.getReminderTemplate(prismTemplate) != null) {
                templatesWithReminders.add(template);
            }
        }
        for (NotificationTemplate template : templatesWithReminders) {
            PrismNotificationTemplate reminder = PrismNotificationTemplate.getReminderTemplate(template.getId());
            NotificationTemplate reminderTemplate = notificationService.getById(reminder);
            Integer reminderInterval = PrismNotificationTemplate.getReminderInterval(reminder);
            template.setReminderTemplate(reminderTemplate);
            NotificationConfiguration configuration = new NotificationConfiguration().withSystem(system).withReminderInterval(reminderInterval);
            entityService.save(configuration);
        }
    }
    
    private void initialiseRoles() {
        for (PrismRole prismRole : PrismRole.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismRole.getScope());
            Role role = new Role().withId(prismRole).withScope(scope);
            entityService.save(role);
        }
    }
    
    private void initialiseStates(System system) {
        for (PrismState prismState : PrismState.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismState.getScope());
            State state = new State().withId(prismState).withScope(scope);
            entityService.save(state);
            StateDuration stateDuration = new StateDuration().withSystem(system).withState(state).withDuration(prismState.getDuration());
            entityService.save(stateDuration);
        }
        for (PrismState prismState : PrismState.values()) {
            State childState = stateService.getById(prismState);
            State parentState = stateService.getById(PrismState.getParentState(prismState));
            childState.setParentState(parentState);
        }
    }
    
    private void initialiseStateTransitionEvaluations() {
        for (PrismStateTransitionEvaluation prismEvaluation : PrismStateTransitionEvaluation.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismEvaluation.getScope());
            StateTransitionEvaluation evaluation = new StateTransitionEvaluation().withId(prismEvaluation).withMethodName(prismEvaluation.getMethodName())
                    .withScope(scope);
            entityService.save(evaluation);
        }
    }

    private String getFileContent(String filePath) {
        try {
            return Joiner.on(java.lang.System.lineSeparator()).join(Files.readLines(new File(filePath), Charsets.UTF_8));
        } catch (IOException e) {
            throw new Error("Could not access default notification template", e);
        }
    }

}
