package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.PrismScope;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RegistrationService {
    // TODO fix tests

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private RefereeDAO refereeDAO;

    @Autowired
    private MailSendingService mailService;

    @Autowired
    private EntityService entityService;

    public User submitRegistration(User pendingUser, PrismScope prismScope) {
        User user = userService.getUserByEmail(pendingUser.getEmail());

        if (user != null) {
            // invited user
            if (!user.getActivationCode().equals(pendingUser.getActivationCode())) {
                throw new ResourceNotFoundException();
            }
        } else {
            // new user
            user = pendingUser;
            user.getAccount().setEnabled(false);
            user.setActivationCode(encryptionUtils.generateUUID());
            userService.save(user);
        }

        user.getAccount().setPassword(encryptionUtils.getMD5Hash(pendingUser.getPassword()));
        mailService.sendRegistrationConfirmation(user);
        return user;
    }

    public User activateAccount(String activationCode, ApplicationFormAction action, int resourceId) {
        User user = userService.getUserByActivationCode(activationCode);
        user.getAccount().setEnabled(true);

        PrismScope scope = entityService.getBy(action.getScopeClass(), "id", resourceId);
        Role role = roleService.getCreatorRole(action, scope);
        roleService.getOrCreateUserRole(scope, user, role.getId());
        return user;
    }

    public void resendConfirmationEmail(User newUser) {
        mailService.sendRegistrationConfirmation(newUser);
    }

    public User findUserForActivationCode(String activationCode) {
        return userService.getUserByActivationCode(activationCode);
    }

    Map<String, Object> modelMap() {
        return new HashMap<String, Object>();
    }

}