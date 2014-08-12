package com.zuehlke.pgadmissions.services;

import java.util.*;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.rest.ActionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowEngineException;
import com.zuehlke.pgadmissions.rest.dto.UserRegistrationDTO;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RegistrationService {

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private ActionService actionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private RoleService roleService;

    public User submitRegistration(UserRegistrationDTO registrationDTO) throws WorkflowEngineException {
        User user = userService.getOrCreateUser(registrationDTO.getFirstName(), registrationDTO.getLastName(), registrationDTO.getEmail());
        if ((registrationDTO.getActivationCode() != null && !user.getActivationCode().equals(registrationDTO.getActivationCode()))
                || user.getUserAccount() != null) {
            throw new ResourceNotFoundException();
        }

        user.setUserAccount(new UserAccount().withPassword(encryptionUtils.getMD5Hash(registrationDTO.getPassword())).withEnabled(false));

        Action action = actionService.getById(registrationDTO.getAction().getActionId());

        Resource resource;
        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
            Object newResourceDTO = registrationDTO.getAction().getAvailableResource();
            ActionOutcome actionOutcome = resourceService.createResource(user, action, newResourceDTO);
            resource = actionOutcome.getTransitionResource();
            action = actionOutcome.getTransitionAction();
        } else {
            resource = entityService.getById(action.getScope().getId().getResourceClass(), registrationDTO.getResourceId());
        }

        sendConfirmationEmail(user, resource, action);
        return user;
    }

    public User activateAccount(String activationCode) {
        User user = userService.getUserByActivationCode(activationCode);
        user.getUserAccount().setEnabled(true);
        return user;
    }

    public void sendConfirmationEmail(User newUser, Resource resource, Action action) {
        NotificationTemplate confirmationTemplate = notificationService.getById(PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        notificationService.sendNotification(newUser, resource, action, confirmationTemplate);
    }

    public User findUserForActivationCode(String activationCode) {
        return userService.getUserByActivationCode(activationCode);
    }

    Map<String, Object> modelMap() {
        return new HashMap<String, Object>();
    }

}
