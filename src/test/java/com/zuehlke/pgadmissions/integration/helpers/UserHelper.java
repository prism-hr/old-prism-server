package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.zuehlke.pgadmissions.rest.ActionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.RoleService;

@Service
@Transactional
public class UserHelper {

    @Autowired
    private MailSenderMock mailSenderMock;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RoleService roleService;

    public void registerAndActivateUser(PrismAction actionId, Integer resourceId, User user, PrismNotificationTemplate activationTemplate) throws WorkflowEngineException {
        if (user.getUserAccount() != null && user.getUserAccount().getPassword() == null) {
            throw new IllegalStateException("User already registered");
        }

        mailSenderMock.assertEmailSent(user, activationTemplate);

        registrationService.submitRegistration(new UserRegistrationDTO().withFirstName(user.getFirstName())
                .withLastName(user.getLastName()).withEmail(user.getEmail()).withActivationCode(user.getActivationCode())
                .withPassword("password").withResourceId(resourceId).withAction(new ActionDTO().withAction(actionId)));

        mailSenderMock.assertEmailSent(user, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);

        registrationService.activateAccount(user.getActivationCode());

        assertTrue(user.isEnabled());
    }

    public void addRoles(Resource resource, User user, PrismRole... roles) throws WorkflowEngineException {
        List<AbstractResourceRepresentation.RoleRepresentation> roleRepresentations = Lists.newArrayList();
        for (PrismRole role : roles) {
            roleRepresentations.add(new AbstractResourceRepresentation.RoleRepresentation(role, true));
        }
        roleService.updateRoles(resource, user, roleRepresentations);
    }

    public void registerAndActivateUserInRoles(PrismAction createAction, Resource resource, User user, PrismNotificationTemplate activationTemplate, PrismRole... roles) throws WorkflowEngineException {
        addRoles(resource, user, roles);
        registerAndActivateUser(createAction, resource.getId(), user, activationTemplate);
    }

}
