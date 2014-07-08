package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.zuehlke.pgadmissions.domain.*;
import com.zuehlke.pgadmissions.domain.System;
import com.zuehlke.pgadmissions.rest.dto.ProgramDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ActionOutcome;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mail.MailService;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.RegistrationDetails;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RegistrationService {

    private static final Pattern createActionPattern = Pattern.compile("(\\w+)_CREATE_(\\w+)");

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private ActionService actionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private MailService notificationService;

    @Autowired
    private EntityService entityService;
    
    @Autowired
    private SystemService systemService;
    
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
        PrismAction registrationAction = registrationDetails.getRegistrationAction();
        if (registrationAction != null) {
            Class<? extends Resource> resourceClass = registrationAction.getScope().getResourceClass();
            resource = entityService.getById(resourceClass, registrationDetails.getResourceId());
            if (createActionPattern.matcher(registrationAction.name()).matches()) {
                resource = createResource(resource, user, registrationAction.getCreationScope(), registrationDetails);
            }
            Action action = entityService.getByProperty(Action.class, "id", registrationAction);
            Comment comment = new Comment().withUser(user).withCreatedTimestamp(new DateTime()).withAction(action).withDeclinedResponse(false);
            ActionOutcome actionOutcome = actionService.executeAction((Resource) resource, registrationAction, comment);
            resource = actionOutcome.getResource();
        } else {
            resource = systemService.getSystem();
        }
        return resource;
    }

    private Resource createResource(Resource parentResource, User user, PrismScope creationScope, RegistrationDetails registrationDetails) {
        switch (creationScope) {
            case INSTITUTION:
                InstitutionDTO institutionDTO = registrationDetails.getNewInstitution();
                return resourceService.createNewInstitution((System)parentResource, user, institutionDTO);
            case PROGRAM:
                ProgramDTO programDTO = registrationDetails.getNewProgram();
                return resourceService.createNewProgram((Institution)parentResource, user, programDTO);
            case APPLICATION:
                return resourceService.createNewApplication((Advert)parentResource, user);
            default:
                throw new IllegalArgumentException(creationScope.name());
        }
    }

    public User activateAccount(String activationCode) {
        User user = userService.getUserByActivationCode(activationCode);
        user.getUserAccount().setEnabled(true);
        return user;
    }

    public void sendConfirmationEmail(User newUser, Resource resource) {
        notificationService.sendEmailNotification(newUser, resource, PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST, null);
    }

    public User findUserForActivationCode(String activationCode) {
        return userService.getUserByActivationCode(activationCode);
    }

    Map<String, Object> modelMap() {
        return new HashMap<String, Object>();
    }

}
