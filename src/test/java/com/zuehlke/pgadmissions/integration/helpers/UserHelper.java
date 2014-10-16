package com.zuehlke.pgadmissions.integration.helpers;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.rest.dto.ActionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Service
@Transactional
public class UserHelper {

    @Autowired
    private MailSenderMock mailSenderMock;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    public void registerAndActivateUser(PrismAction actionId, Integer resourceId, User user, PrismNotificationTemplate activationTemplate) throws Exception {
        if (user.getUserAccount() != null && user.getUserAccount().getPassword() == null) {
            throw new IllegalStateException("User already registered");
        }

        mailSenderMock.assertEmailSent(user, activationTemplate);

        String testContextReferrer = actionId.getActionCategory() == PrismActionCategory.CREATE_RESOURCE ? "http://www.testcontextreferrer.com" : null;

        userService.registerUser(
                new UserRegistrationDTO().withFirstName(user.getFirstName()).withLastName(user.getLastName()).withEmail(user.getEmail())
                        .withActivationCode(user.getActivationCode()).withPassword("password").withResourceId(resourceId)
                        .withAction(new ActionDTO().withAction(actionId)), testContextReferrer);

        mailSenderMock.assertEmailSent(user, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);

        PrismScope resourceScope = actionId.getCreationScope() == null ? actionId.getScope() : actionId.getCreationScope();
        Resource resource = resourceService.getById(resourceScope.getResourceClass(), resourceId);
        assertEquals(testContextReferrer, resource.getReferrer());

        userService.activateUser(user.getId(), actionId, resourceId);
        assertTrue(user.isEnabled());
    }

}
