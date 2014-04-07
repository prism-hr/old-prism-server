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
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
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
    private UserDAO userDAO;

    @Autowired
    private RefereeDAO refereeDAO;

    @Autowired
    private MailSendingService mailService;

    public RegisteredUser processPendingApplicantUser(RegisteredUser pendingApplicantUser, String queryString) {
        pendingApplicantUser.setUsername(pendingApplicantUser.getEmail());
        pendingApplicantUser.setPassword(encryptionUtils.getMD5Hash(pendingApplicantUser.getPassword()));
        pendingApplicantUser.setEnabled(false);
        pendingApplicantUser.setOriginalApplicationQueryString(queryString);
        pendingApplicantUser.setActivationCode(encryptionUtils.generateUUID());
        return pendingApplicantUser;
    }

    public RegisteredUser updateOrSaveUser(RegisteredUser pendingUser, String queryString) {
        RegisteredUser user = null;
        if (StringUtils.isNotEmpty(pendingUser.getActivationCode())) {
            // User has been invited to join PRISM
            user = userDAO.getUserByActivationCode(pendingUser.getActivationCode());
            user.setPassword(encryptionUtils.getMD5Hash(pendingUser.getPassword()));
            user.setUsername(user.getEmail());
        } else {
            // User is an applicant
            user = processPendingApplicantUser(pendingUser, queryString);
            // FIXME add applicant role to the user
            userDAO.save(user);
        }

        sendConfirmationEmail(user);
        return user;
    }

    public void sendInstructionsToRegisterIfActivationCodeIsMissing(final RegisteredUser user) {
        Referee referee = refereeDAO.getRefereeByUser(user);

        if (!user.getPendingRoleNotifications().isEmpty()) {
            for (PendingRoleNotification notification : user.getPendingRoleNotifications()) {
                notification.setNotificationDate(null);
            }
            userDAO.save(user);
        } else if (referee != null) {
            referee.setLastNotified(null);
            refereeDAO.save(referee);
        }
    }

    public void sendConfirmationEmail(RegisteredUser newUser) {
        try {
            mailService.sendRegistrationConfirmation(newUser);
        } catch (Exception e) {
            log.warn("{}", e);
        }
    }

    public RegisteredUser findUserForActivationCode(String activationCode) {
        return userDAO.getUserByActivationCode(activationCode);
    }

    Map<String, Object> modelMap() {
        return new HashMap<String, Object>();
    }

}