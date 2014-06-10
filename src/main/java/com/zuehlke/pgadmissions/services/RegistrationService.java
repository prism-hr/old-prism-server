package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate.SYSTEM_COMPLETE_REGISTRATION_REQUEST;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mail.NotificationService;
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
    private NotificationService notificationService;

    @Autowired
    private EntityService entityService;

    public User submitRegistration(User pendingUser, PrismResource prismScope) {
        User user = userService.getOrCreateUser(pendingUser.getFirstName(), pendingUser.getLastName(), pendingUser.getEmail());

        if (pendingUser.getActivationCode() != null && !user.getActivationCode().equals(pendingUser.getActivationCode())) {
            throw new ResourceNotFoundException();
        }

        user.getUserAccount().setPassword(encryptionUtils.getMD5Hash(pendingUser.getPassword()));
        sendConfirmationEmail(user);
        return user;
    }

    public User activateAccount(String activationCode) {
        User user = userService.getUserByActivationCode(activationCode);
        user.getUserAccount().setEnabled(true);
        return user;
    }

    public void sendConfirmationEmail(User newUser) {
        notificationService.sendEmailNotification(newUser, null, SYSTEM_COMPLETE_REGISTRATION_REQUEST, null);
    }

    public User findUserForActivationCode(String activationCode) {
        return userService.getUserByActivationCode(activationCode);
    }

    Map<String, Object> modelMap() {
        return new HashMap<String, Object>();
    }

}