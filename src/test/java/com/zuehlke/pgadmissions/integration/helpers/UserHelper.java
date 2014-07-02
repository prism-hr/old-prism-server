package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.domain.ResourceRepresentation;
import com.zuehlke.pgadmissions.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.services.RegistrationService;

import java.util.List;

@Service
@Transactional
public class UserHelper {

    @Autowired
    private MailSenderMock mailSenderMock;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RoleService roleService;

    public void registerAndActivateUser(Resource resource, User user, PrismNotificationTemplate activationTemplate) {
        if (user.getUserAccount() != null) {
            throw new IllegalStateException("User already registered");
        }
        
        mailSenderMock.assertEmailSent(user, activationTemplate);

        registrationService.submitRegistration(user.withAccount(new UserAccount().withPassword("password")), resource);

        mailSenderMock.assertEmailSent(user, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);

        registrationService.activateAccount(user.getActivationCode());
        
        assertTrue(user.isEnabled());
    }

    public void addRoles(Resource resource, User user, PrismRole... roles) {
        List<ResourceRepresentation.RoleRepresentation> roleRepresentations = Lists.newArrayList();
        for (PrismRole role : roles) {
            roleRepresentations.add(new ResourceRepresentation.RoleRepresentation(role, true));
        }
        roleService.updateRoles(resource, user, roleRepresentations);
    }

    public void registerAndActivateUserInRoles(Resource resource, User user, PrismNotificationTemplate activationTemplate, PrismRole... roles ){
        addRoles(resource, user, roles);
        registerAndActivateUser(resource, user, activationTemplate);
    }

}
