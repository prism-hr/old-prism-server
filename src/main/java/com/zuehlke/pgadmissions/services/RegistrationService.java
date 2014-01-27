package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.zuehlke.pgadmissions.mail.MailSendingService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class RegistrationService {

	private final Logger log = LoggerFactory.getLogger(RegistrationService.class);

	private final EncryptionUtils encryptionUtils;

	private final RoleDAO roleDAO;

	private final UserDAO userDAO;

	private final InterviewerDAO interviewerDAO;

	private final ReviewerDAO reviewerDAO;

	private final SupervisorDAO supervisorDAO;

	private final RefereeDAO refereeDAO;

	private final MailSendingService mailService;

	public RegistrationService() {
		this(null, null, null, null, null, null, null, null);
	}

	@Autowired
	public RegistrationService(final EncryptionUtils encryptionUtils, final RoleDAO roleDAO, final UserDAO userDAO, final InterviewerDAO interviewerDAO, final ReviewerDAO reviewerDAO, final SupervisorDAO supervisorDAO, final RefereeDAO refereeDAO, final MailSendingService mailService) {
		this.encryptionUtils = encryptionUtils;
		this.roleDAO = roleDAO;
		this.userDAO = userDAO;
		this.interviewerDAO = interviewerDAO;
		this.reviewerDAO = reviewerDAO;
		this.mailService = mailService;
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
		}

		try {
			userDAO.save(user);
		} catch (Exception e) {
			log.error("Could not save user: {}", user.getEmail());
			return null;
		}

		sendConfirmationEmail(user);
		return user;
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