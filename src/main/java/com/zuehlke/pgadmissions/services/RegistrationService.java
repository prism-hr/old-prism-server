package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.AdvertType;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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

    public User processPendingApplicantUser(User user, Advert advert) {
        user.getAccount().setPassword(encryptionUtils.getMD5Hash(user.getPassword()));
        user.getAccount().setEnabled(false);
        user.setActivationCode(encryptionUtils.generateUUID());

        userService.save(user);

        Authority authority = advert.getAdvertType() == AdvertType.PROGRAM ? Authority.PROGRAM_APPLICATION_CREATOR
                : Authority.PROJECT_APPLICATION_CREATOR;
        roleService.getOrCreateUserRole(advert, user, authority);

        return user;
    }

    public User submitRegistration(User pendingUser, Advert advert) {
        User user = null;

        // TODO use action ID instead of activation code
        boolean isInvited = StringUtils.isNotEmpty(pendingUser.getActivationCode());

        if (isInvited) {
            // User has been invited to join PRISM
            user = userService.getUserByActivationCode(pendingUser.getActivationCode());
            user.getAccount().setPassword(encryptionUtils.getMD5Hash(pendingUser.getPassword()));
        } else {
            // User is an applicant
            user = processPendingApplicantUser(pendingUser, advert);
        }

        mailService.sendRegistrationConfirmation(user);
        return user;
    }

    public User activateAccount(String activationCode) {
        User user = userService.getUserByActivationCode(activationCode);
        user.getAccount().setEnabled(true);
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