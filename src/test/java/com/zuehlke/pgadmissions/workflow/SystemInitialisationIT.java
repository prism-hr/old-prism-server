package com.zuehlke.pgadmissions.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Configuration;
import com.zuehlke.pgadmissions.domain.NotificationConfiguration;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.Scope;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.StateTransitionEvaluation;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserRole;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.NotificationService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.StateService;
import com.zuehlke.pgadmissions.services.SystemService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
public class SystemInitialisationIT {
    
    @Autowired
    private Environment environment;
    
    @Autowired
    private ActionService actionService;
    
    @Autowired
    private NotificationService notificationTemplateService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private StateService stateService;
    
    @Autowired
    private SystemService systemService;
    
    @Test
    public void testSystemInitialisation() {
        for (int i = 0; i < 2; i ++) {
            systemService.initialiseSystem();
            
            System system = systemService.getSystem();
            assertEquals(system.getName(), environment.getProperty("system.name"));
            assertEquals(system.getState().getId(), PrismState.SYSTEM_APPROVED);
            
            User systemUser = system.getUser();
            assertEquals(systemUser.getFirstName(), environment.getProperty("system.user.firstName"));
            assertEquals(systemUser.getLastName(), environment.getProperty("system.user.lastName"));
            assertEquals(systemUser.getEmail(), environment.getProperty("system.user.email"));
    
            for (UserRole userRole : systemUser.getUserRoles()) {
                assertEquals(userRole.getRole().getId(), PrismRole.SYSTEM_ADMINISTRATOR);
            }
            
            for (Scope scope : systemService.getScopes()) {
                assertEquals(scope.getId().getPrecedence(), scope.getPrecedence());
            }
            
            for (Action action : actionService.getActions()) {
                assertEquals(action.getId().getActionType(), action.getActionType());
                assertEquals(action.getId().getScope(), action.getScope().getId());
            }
            
            for (Configuration configuration : systemService.getConfigurations()) {
                assertEquals(configuration.getParameter().getDefaultValue(), configuration.getValue());
            }
            
            for (NotificationTemplate template : notificationTemplateService.getTemplates()) {
                assertEquals(template.getId().getNotificationType(), template.getNotificationType());
                assertEquals(template.getId().getNotificationPurpose(), template.getNotificationPurpose());
                assertEquals(template.getId().getScope(), template.getScope().getId());
                assertEquals(PrismNotificationTemplate.getReminderTemplate(template.getId()), template.getReminderTemplate().getId());
                
                NotificationConfiguration configuration = notificationTemplateService.getConfiguration(system, template);
                assertNotNull(configuration);
                assertNotNull(configuration.getNotificationTemplateVersion());
                assertEquals(PrismNotificationTemplate.getReminderInterval(template.getId()), configuration.getReminderInterval());
                
                NotificationTemplateVersion version = configuration.getNotificationTemplateVersion();
                assertEquals(getFileContent(template.getId().getInitialTemplateSubject()), version.getSubject());
                assertEquals(getFileContent(template.getId().getInitialTemplateContent()), version.getContent());
            }
            
            for (Role role : roleService.getRoles()) {
                assertEquals(role.getId().getScope(), role.getScope());
            }
            
            for (State state : stateService.getStates()) {
                assertEquals(PrismState.getParentState(state.getId()), state.getParentState());
                assertEquals(state.getId().getScope(), state.getScope());
                assertEquals(state.getId().getDuration(), systemService.getStateDuration(state));         
            }
            
            for (StateTransitionEvaluation evaluation : stateService.getTransitionEvaluations()) {
                assertEquals(evaluation.getId().getMethodName(), evaluation.getMethodName());
                assertEquals(evaluation.getId().getScope(), evaluation.getScope());
            }
            
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
