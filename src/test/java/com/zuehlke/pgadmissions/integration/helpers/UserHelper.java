package com.zuehlke.pgadmissions.integration.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition.SYSTEM_COMPLETE_REGISTRATION_REQUEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.rest.dto.ActionDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.AuthenticationService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class UserHelper {

    @Inject
    private MailSenderMock mailSenderMock;

    @Inject
    private ActionService actionService;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private UserService userService;

    public void registerAndActivateUser(PrismAction actionId, Integer resourceId, User user, PrismNotificationDefinition activationTemplate) throws Exception {
        if (user.getUserAccount() != null && user.getUserAccount().getPassword() == null) {
            throw new IllegalStateException("User already registered");
        }

        mailSenderMock.assertEmailSent(user, activationTemplate);

        String testContextReferrer = actionId.getActionCategory() == CREATE_RESOURCE ? "http://www.testcontextreferrer.com" : null;

        authenticationService.registerUser(
                new UserRegistrationDTO().withFirstName(user.getFirstName()).withLastName(user.getLastName()).withEmail(user.getEmail())
                        .withActivationCode(user.getActivationCode()).withPassword("password").withResourceId(resourceId)
                        .withAction(new ActionDTO().withActionId(actionId)), null);

        mailSenderMock.assertEmailSent(user, SYSTEM_COMPLETE_REGISTRATION_REQUEST);

        Action action = actionService.getById(actionId);
        PrismScope resourceScope = action.getCreationScope() == null ? actionId.getScope() : action.getCreationScope().getId();
        Resource resource = resourceService.getById(resourceScope.getResourceClass(), resourceId);
        assertEquals(testContextReferrer, resource.getReferrer());

        userService.activateUser(user.getId(), actionId, resourceId);
        assertTrue(user.isEnabled());
    }

}
