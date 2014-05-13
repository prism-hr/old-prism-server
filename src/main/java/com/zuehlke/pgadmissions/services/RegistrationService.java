package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RegistrationService {
    // TODO fix tests

    private final Logger log = LoggerFactory.getLogger(RegistrationService.class);

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

    public User processPendingApplicantUser(User pendingApplicantUser) {
        pendingApplicantUser.getAccount().setPassword(encryptionUtils.getMD5Hash(pendingApplicantUser.getPassword()));
        pendingApplicantUser.getAccount().setEnabled(false);
        // FIXME set advert ID
        // pendingApplicantUser.setOriginalApplicationQueryString(advertId);
        pendingApplicantUser.setActivationCode(encryptionUtils.generateUUID());
        return pendingApplicantUser;
    }

    public User submitRegistration(User pendingUser) {
        User user = null;

        // TODO use action ID instead of activation code
        boolean isInvited = StringUtils.isNotEmpty(pendingUser.getActivationCode());

        if (isInvited) {
            // User has been invited to join PRISM
            user = userService.getUserByActivationCode(pendingUser.getActivationCode());
            user.getAccount().setPassword(encryptionUtils.getMD5Hash(pendingUser.getPassword()));
        } else {
            // User is an applicant
            user = processPendingApplicantUser(pendingUser);
            // FIXME add program_application_creator or project_application_creator role to the user
            userService.save(user);
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