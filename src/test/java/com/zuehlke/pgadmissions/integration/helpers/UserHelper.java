package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.mail.MailSenderMock;
import com.zuehlke.pgadmissions.rest.dto.ActionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

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

        userService.activateUser(user.getId());
        assertTrue(user.isEnabled());
    }

    public void addRoles(Resource resource, User user, PrismRole... roles) throws Exception {
        List<AbstractResourceRepresentation.RoleRepresentation> roleRepresentations = Lists.newArrayList();
        for (PrismRole role : roles) {
            roleRepresentations.add(new AbstractResourceRepresentation.RoleRepresentation(role, true));
        }
        roleService.updateUserRoles(resource, user, roleRepresentations);
    }

    public void registerAndActivateUserInRoles(PrismAction createAction, Resource resource, User user, PrismNotificationTemplate activationTemplate,
            PrismRole... roles) throws Exception {
        addRoles(resource, user, roles);
        registerAndActivateUser(createAction, resource.getId(), user, activationTemplate);
    }

}
