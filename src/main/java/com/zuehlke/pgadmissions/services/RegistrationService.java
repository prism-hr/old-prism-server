package com.zuehlke.pgadmissions.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	private final String host;
	
	public RegistrationService() {
		this(null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public RegistrationService(
	        final EncryptionUtils encryptionUtils, 
	        final RoleDAO roleDAO, 
	        final UserDAO userDAO,
			final InterviewerDAO interviewerDAO, 
			final ReviewerDAO reviewerDAO, 
			final SupervisorDAO supervisorDAO, 
			final RefereeDAO refereeDAO,
			final MailSendingService mailService,
			@Value("${application.host}") final String host) {
		this.encryptionUtils = encryptionUtils;
		this.roleDAO = roleDAO;
		this.userDAO = userDAO;
		this.interviewerDAO = interviewerDAO;
		this.reviewerDAO = reviewerDAO;
		this.mailService = mailService;
		this.supervisorDAO = supervisorDAO;
		this.refereeDAO = refereeDAO;
		this.host = host;
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
		try {
			userDAO.save(user);
		}
		catch (Exception e) {
			log.error("Could not save user: {}", user.getEmail());
			return;
		}

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
			mailService.sendRegistrationConfirmation(newUser, getRegistrationConfirmationAction(newUser));
		} catch (Exception e) {
			log.warn("{}", e);
		}
	}

	Map<String, Object> populateModelForRegistrationConfirmation(RegisteredUser newUser) {
		Map<String, Object> model = modelMap();
		model.put("user", newUser);
		model.put("host", host);
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