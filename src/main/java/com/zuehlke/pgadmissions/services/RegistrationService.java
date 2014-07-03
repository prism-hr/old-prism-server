package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import com.zuehlke.pgadmissions.domain.UserAccount;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.dto.RegistrationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mail.MailService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RegistrationService {

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefereeDAO refereeDAO;

    @Autowired
    private MailService notificationService;

    @Autowired
    private EntityService entityService;

    public User submitRegistration(RegistrationDetails registrationDetails) {
        Class<? extends Resource> resourceClass = registrationDetails.getResourceType().getResourceClass();
        Resource resource = entityService.getById(resourceClass, registrationDetails.getResourceId());
        User user = userService.getOrCreateUser(registrationDetails.getFirstName(), registrationDetails.getLastName(), registrationDetails.getEmail());

        if (registrationDetails.getActivationCode() != null && !user.getActivationCode().equals(registrationDetails.getActivationCode())) {
            throw new ResourceNotFoundException();
        }

        if(user.getUserAccount() == null) {
            user.setUserAccount(new UserAccount());
        }

        user.getUserAccount().setPassword(encryptionUtils.getMD5Hash(registrationDetails.getPassword()));
        sendConfirmationEmail(user, resource);
        return user;
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
