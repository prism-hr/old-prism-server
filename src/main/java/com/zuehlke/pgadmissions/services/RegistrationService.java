package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.InterviewerDAO;
import com.zuehlke.pgadmissions.dao.RefereeDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.SupervisorDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Interviewer;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Supervisor;

@Service
@Transactional
public class RegistrationService {
	
	private final UserDAO userDAO;

	private final InterviewerDAO interviewerDAO;
	
	private final ReviewerDAO reviewerDAO;
	
	private final SupervisorDAO supervisorDAO;
	
	private final RefereeDAO refereeDAO;
	
	private final UserService userService;
	
	public RegistrationService() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public RegistrationService(
	        final UserDAO userDAO,
			final InterviewerDAO interviewerDAO, 
			final ReviewerDAO reviewerDAO, 
			final SupervisorDAO supervisorDAO, 
			final RefereeDAO refereeDAO,
			final UserService userService) {
		this.userDAO = userDAO;
		this.interviewerDAO = interviewerDAO;
		this.reviewerDAO = reviewerDAO;
		this.supervisorDAO = supervisorDAO;
		this.refereeDAO = refereeDAO;
		this.userService = userService;
	}

	public RegisteredUser updateOrSaveUser(RegisteredUser pendingUser, String queryString) {
		return userService.enableRegisteredUser(pendingUser, queryString);
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

}