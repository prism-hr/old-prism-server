package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
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

    private final String EMAIL_DEFAULT_SUBJECT_DIRECTORY = "email/subject/";

    private final String EMAIL_DEFAULT_CONTENT_DIRECTORY = "email/content/";

    @Value("${system.name}")
    private String systemName;

    @Value("${system.user.firstName}")
    private String systemUserFirstName;

    @Value("${system.user.lastName}")
    private String systemUserLastName;

    @Value("${system.user.email}")
    private String systemUserEmail;

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
        return (System) entityService.getByProperty(com.zuehlke.pgadmissions.domain.System.class, "name", systemName);
    }

    public System getOrCreateSystem(User systemUser) {
        State systemRunning = stateService.getById(PrismState.SYSTEM_APPROVED);
        System transientSystem = new System().withName(systemName).withUser(systemUser).withState(systemRunning);
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
        initialiseScopes();
        initialiseActions();
        initialiseRoles();
        initialiseStates();
        initialiseStateTransitionEvaluations();

        User systemUser = userService.getOrCreateUser(systemUserFirstName, systemUserLastName, systemUserEmail);
        System system = getOrCreateSystem(systemUser);

        Role systemRole = roleService.getById(PrismRole.SYSTEM_ADMINISTRATOR);
        roleService.getOrCreateUserRole(system, systemUser, systemRole);

        initialiseConfigurations(system);
        initialiseNotificationTemplates(system);
        initialiseStateDurations(system);

        if (systemUser.getUserAccount() == null) {
            mailService.sendEmailNotification(systemUser, system, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        }
        
        entityService.flush();
    }

    private void initialiseScopes() {
        for (PrismScope prismScope : PrismScope.values()) {
            Scope transientScope = new Scope().withId(prismScope).withPrecedence(prismScope.getPrecedence());
            entityService.getOrCreate(transientScope);
        }
    }

    private void initialiseActions() {
        for (PrismAction prismAction : PrismAction.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismAction.getScope());
            Action transientAction = new Action().withId(prismAction).withActionType(prismAction.getActionType()).withScope(scope);
            entityService.getOrCreate(transientAction);
        }
    }

    private void initialiseRoles() {
        for (PrismRole prismRole : PrismRole.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismRole.getScope());
            Role transientRole = new Role().withId(prismRole).withScope(scope);
            entityService.getOrCreate(transientRole);
        }
    }

    private void initialiseStates() {
        for (PrismState prismState : PrismState.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismState.getScope());
            State transientState = new State().withId(prismState).withScope(scope);
            entityService.getOrCreate(transientState);
        }
        for (PrismState prismState : PrismState.values()) {
            State childState = stateService.getById(prismState);
            State parentState = stateService.getById(PrismState.getParentState(prismState));
            childState.setParentState(parentState);
        }
    }

    private void initialiseConfigurations(System system) {
        for (PrismConfiguration prismConfiguration : PrismConfiguration.values()) {
            Configuration transientConfiguration = new Configuration().withSystem(system).withParameter(prismConfiguration)
                    .withValue(prismConfiguration.getDefaultValue());
            entityService.getOrCreate(transientConfiguration);
        }
    }

    private void initialiseNotificationTemplates(System system) {
        HashMap<NotificationTemplate, NotificationTemplateVersion> createdTemplates = Maps.newHashMap();
        for (PrismNotificationTemplate prismTemplate : PrismNotificationTemplate.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismTemplate.getScope());
            
            NotificationTemplate template;
            NotificationTemplate transientTemplate = new NotificationTemplate().withId(prismTemplate).withNotificationType(prismTemplate.getNotificationType())
                    .withNotificationPurpose(prismTemplate.getNotificationPurpose()).withScope(scope);
            NotificationTemplate duplicateTemplate = entityService.getDuplicateEntity(transientTemplate);
            NotificationTemplateVersion version;
            
            if (duplicateTemplate == null) {
                entityService.save(transientTemplate);
                template = transientTemplate;
                String defaultSubject = getFileContent(EMAIL_DEFAULT_SUBJECT_DIRECTORY + prismTemplate.getInitialTemplateSubject());
                String defaultContent = getFileContent(EMAIL_DEFAULT_CONTENT_DIRECTORY + prismTemplate.getInitialTemplateContent());
                version = new NotificationTemplateVersion().withNotificationTemplate(template).withSubject(defaultSubject).withContent(defaultContent)
                        .withCreatedTimestamp(new DateTime());
                entityService.save(version);
            } else {
                template = duplicateTemplate;
                version = notificationService.getActiveVersion(system, template);
                if (version == null) {
                    version = notificationService.getLatestVersion(system, template);
                }
            }
            
            createdTemplates.put(template, version);
        }

        for (NotificationTemplate template : createdTemplates.keySet()) {
            template.setReminderTemplate(notificationService.getById(PrismNotificationTemplate.getReminderTemplate(template.getId())));
            NotificationConfiguration transientConfiguration = new NotificationConfiguration().withSystem(system).withNotificationTemplate(template)
                    .withNotificationTemplateVersion(createdTemplates.get(template))
                    .withReminderInterval(PrismNotificationTemplate.getReminderInterval(template.getId()));
            entityService.getOrCreate(transientConfiguration);
        }
    }

    private void initialiseStateDurations(System system) {
        for (PrismState prismState : PrismState.values()) {
            if (prismState.getDuration() != null) {
                State state = stateService.getById(prismState);
                StateDuration transientStateDuration = new StateDuration().withSystem(system).withState(state).withDuration(prismState.getDuration());
                entityService.getOrCreate(transientStateDuration);
            }
        }
    }

    private void initialiseStateTransitionEvaluations() {
        for (PrismStateTransitionEvaluation prismEvaluation : PrismStateTransitionEvaluation.values()) {
            Scope scope = entityService.getByProperty(Scope.class, "id", prismEvaluation.getScope());
            StateTransitionEvaluation transientEvaluation = new StateTransitionEvaluation().withId(prismEvaluation)
                    .withMethodName(prismEvaluation.getMethodName()).withScope(scope);
            entityService.getOrCreate(transientEvaluation);
        }
    }

    private String getFileContent(String filePath) {
        try {
            return Joiner.on(java.lang.System.lineSeparator()).join(Resources.readLines(Resources.getResource(filePath), Charsets.UTF_8));
        } catch (IOException e) {
            throw new Error("Could not access default notification template", e);
        }
    }

}
