package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.mail.MimeMessagePreparatorFactory;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.utils.Environment;

@Service
@Transactional
public class RegistrationService {

    private final EncryptionUtils encryptionUtils;
    private final RoleDAO roleDAO;
    private final UserDAO userDAO;

    private final InterviewerDAO interviewerDAO;
    private final ReviewerDAO reviewerDAO;
    private final SupervisorDAO supervisorDAO;
    private final RefereeDAO refereeDAO;

    private final JavaMailSender mailsender;
    private final MimeMessagePreparatorFactory mimeMessagePreparatorFactory;
    private final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    private final MessageSource msgSource;

    public RegistrationService() {
        this(null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public RegistrationService(EncryptionUtils encryptionUtils, RoleDAO roleDAO, UserDAO userDAO,
            MimeMessagePreparatorFactory mimeMessagePreparatorFactory, JavaMailSender mailsender,
            MessageSource msgSource, InterviewerDAO interviewerDAO, ReviewerDAO reviewerDAO,
            SupervisorDAO supervisorDAO, RefereeDAO refereeDAO) {
        this.encryptionUtils = encryptionUtils;
        this.roleDAO = roleDAO;
        this.userDAO = userDAO;
        this.mimeMessagePreparatorFactory = mimeMessagePreparatorFactory;
        this.mailsender = mailsender;
        this.msgSource = msgSource;
        this.interviewerDAO = interviewerDAO;
        this.reviewerDAO = reviewerDAO;
        this.supervisorDAO = supervisorDAO;
        this.refereeDAO = refereeDAO;
    }

    public RegisteredUser processPendingApplicantUser(RegisteredUser pendingApplicantUser, String queryString) {
        pendingApplicantUser.setUsername(pendingApplicantUser.getEmail());
        pendingApplicantUser.setPassword(encryptionUtils.getMD5Hash(pendingApplicantUser.getPassword()));
        pendingApplicantUser.setAccountNonExpired(true);
        pendingApplicantUser.setAccountNonLocked(true);
        pendingApplicantUser.setEnabled(false);
        pendingApplicantUser.setCredentialsNonExpired(true);
        pendingApplicantUser.setOriginalApplicationQueryString(queryString);
        pendingApplicantUser.getRoles().add(roleDAO.getRoleByAuthority(Authority.APPLICANT));
        pendingApplicantUser.setActivationCode(encryptionUtils.generateUUID());
        return pendingApplicantUser;
    }

    public RegisteredUser processPendingSuggestedUser(RegisteredUser pendingSuggestedUser) {
        pendingSuggestedUser.setPassword(encryptionUtils.getMD5Hash(pendingSuggestedUser.getPassword()));
        pendingSuggestedUser.setUsername(pendingSuggestedUser.getEmail());
        return pendingSuggestedUser;

    }

    public void updateOrSaveUser(RegisteredUser pendingUser, String queryString) {

        RegisteredUser user = null;
        if (pendingUser.getId() != null) {
            user = processPendingSuggestedUser(pendingUser);
        } else {
            user = processPendingApplicantUser(pendingUser, queryString);
        }
        userDAO.save(user);

        sendConfirmationEmail(user);
    }

    public void sendInstructionsToRegisterIfActivationCodeIsMissing(final RegisteredUser user) {
        Interviewer interviewer = interviewerDAO.getInterviewerByUser(user);
        Reviewer reviewer = reviewerDAO.getReviewerByUser(user);
        Supervisor supervisor = supervisorDAO.getSupervisorByUser(user);
        Referee referee = refereeDAO.getRefereeByUser(user);

        if (!user.getPendingRoleNotifications().isEmpty()) {
            for (PendingRoleNotification notification : user.getPendingRoleNotifications()) {
                notification.setNotificationDate(null);
            }
            userDAO.save(user);
        } else if (interviewer != null) {
            interviewer.setLastNotified(null);
            interviewerDAO.save(interviewer);
        } else if (reviewer != null) {
            reviewer.setLastNotified(null);
            reviewerDAO.save(reviewer);
        } else if (supervisor != null) {
            supervisor.setLastNotified(null);
            supervisorDAO.save(supervisor);
        } else if (referee != null) {
            referee.setLastNotified(null);
            refereeDAO.save(referee);
        }
    }

    public void sendConfirmationEmail(RegisteredUser newUser) {
        try {
            Map<String, Object> model = populateModelForRegistrationConfirmation(newUser);
            InternetAddress toAddress = new InternetAddress(newUser.getEmail(), newUser.getFirstName() + " "
                    + newUser.getLastName());
            String subject = msgSource.getMessage("registration.confirmation", null, null);
            mailsender.send(mimeMessagePreparatorFactory.getMimeMessagePreparator(toAddress, subject,
                    "private/pgStudents/mail/registration_confirmation.ftl", model, null));
        } catch (Exception e) {
            log.warn("Error while sending email", e);
        }
    }

    Map<String, Object> populateModelForRegistrationConfirmation(RegisteredUser newUser) {
        Map<String, Object> model = modelMap();
        model.put("user", newUser);
        model.put("host", Environment.getInstance().getApplicationHostName());
        model.put("action", getRegistrationConfirmationAction(newUser));
        return model;
    }

    public RegisteredUser findUserForActivationCode(String activationCode) {
        return userDAO.getUserByActivationCode(activationCode);
    }

    Map<String, Object> modelMap() {
        return new HashMap<String, Object>();
    }

    protected String getRegistrationConfirmationAction(RegisteredUser user) {
        if (user.isInRole(Authority.APPLICANT)) {
            return "complete your application";
        }
        if (user.getDirectToUrl() == null) {
            return "continue";
        }
        if (user.getDirectToUrl().startsWith(DirectURLsEnum.ADD_REFERENCE.displayValue())) {
            return "complete your reference";
        }
        if (user.getDirectToUrl().startsWith(DirectURLsEnum.ADD_REVIEW.displayValue())) {
            return "complete your review";
        }
        return "view the application";
    }

}