package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.RegistrationDetails;
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

    public User submitRegistration(RegistrationDetails registrationDetails) {
        User user = userService.getOrCreateUser(registrationDetails.getFirstName(), registrationDetails.getLastName(), registrationDetails.getEmail());
        if (registrationDetails.getActivationCode() != null && !user.getActivationCode().equals(registrationDetails.getActivationCode())) {
            throw new ResourceNotFoundException();
        }

        if (user.getUserAccount() == null) {
            user.setUserAccount(new UserAccount());
        }
        user.getUserAccount().setPassword(encryptionUtils.getMD5Hash(registrationDetails.getPassword()));

        Resource resource = performRegistrationAction(user, registrationDetails);
        sendConfirmationEmail(user, resource);
        return user;
    }

    private Resource performRegistrationAction(User user, RegistrationDetails registrationDetails) {
        Resource resource = null;
        PrismAction actionId = registrationDetails.getAction();
        
        if (actionId != null) {
            Action action = entityService.getByProperty(Action.class, "id", actionId);
            Class<? extends Resource> resourceClass = actionId.getScope().getResourceClass();
            resource = entityService.getById(resourceClass, registrationDetails.getResourceId());

            Comment comment = new Comment().withUser(user).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false);

            if (action.getActionCategory() == PrismActionCategory.CREATE_RESOURCE) {
                resource = resourceService.createResource(resource, user, actionId.getCreationScope(), registrationDetails);
                Role creatorRole = roleService.getCreatorRole(resource);
                comment.getCommentAssignedUsers().add(new CommentAssignedUser().withUser(user).withRole(creatorRole));
            }
            
            ActionOutcome actionOutcome = null;
            if (action.getActionType() == PrismActionType.USER_INVOCATION) {
                actionOutcome = actionService.executeUserAction(resource, action, comment);
            } else {
                actionOutcome = actionService.executeSystemAction(resource, action, comment);
            }
            
            resource = actionOutcome.getResource();
        }
        
        return resource;
    }

    public User activateAccount(String activationCode) {
        User user = userService.getUserByActivationCode(activationCode);
        user.getUserAccount().setEnabled(true);
        return user;
    }

    public void sendConfirmationEmail(User newUser, Resource resource) {
        notificationService.sendNotification(newUser, resource, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST);
    }

    public User findUserForActivationCode(String activationCode) {
        return userService.getUserByActivationCode(activationCode);
    }

    Map<String, Object> modelMap() {
        return new HashMap<String, Object>();
    }

}
