package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        if (registrationDTO.getActivationCode() != null && !user.getActivationCode().equals(registrationDTO.getActivationCode())) {
            throw new ResourceNotFoundException();
        }

        if (user.getUserAccount() == null) {
            user.setUserAccount(new UserAccount().withEnabled(false));
        }
        user.getUserAccount().setPassword(encryptionUtils.getMD5Hash(registrationDTO.getPassword()));

        Action action = actionService.getById(registrationDTO.getActionId());
        Resource resource = entityService.getById(action.getScope().getId().getResourceClass(), registrationDTO.getResourceId());

        if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
            Object newResourceDTO = unpackNewResourceDTO(registrationDTO);
            resource = resourceService.create(user, action, newResourceDTO);
        }

        sendConfirmationEmail(user, resource);
        return user;
    }

    private Object unpackNewResourceDTO(UserRegistrationDTO registrationDTO) {
        Set<Object> resourceDTOs = Sets.newHashSet();
        resourceDTOs.add(registrationDTO.getNewInstitution());
        resourceDTOs.add(registrationDTO.getNewProgram());
        resourceDTOs.add(registrationDTO.getNewProject());
        resourceDTOs.add(registrationDTO.getNewApplication());

        for (Object resourceDTO : resourceDTOs) {
            if (resourceDTO == null) {
                resourceDTOs.remove(resourceDTO);
            }
        }

        if (resourceDTOs.size() != 1) {
            throw new Error();
        }

        return resourceDTOs.iterator().next();
    }

    public User activateAccount(String activationCode) {
        User user = userService.getUserByActivationCode(activationCode);
        user.getUserAccount().setEnabled(true);
        return user;
    }

    public void sendConfirmationEmail(User newUser, Resource resource) {
        NotificationTemplate confirmationTemplate = notificationService.getById(PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
        notificationService.sendNotification(newUser, resource, confirmationTemplate);
    }

    public User findUserForActivationCode(String activationCode) {
        return userService.getUserByActivationCode(activationCode);
    }

    Map<String, Object> modelMap() {
        return new HashMap<String, Object>();
    }

}
