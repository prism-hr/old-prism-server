package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ReviewRoundDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
public class ReviewService {

	private final RoleDAO roleDAO;
	private final UserDAO userDAO;
	private final ProgramDAO programmeDAO;
	private final ApplicationFormDAO applicationDAO;
	private final ReviewerDAO reviewerDAO;
	private final ReviewRoundDAO reviewRoundDAO;
	

	ReviewService() {
		this(null, null, null, null, null, null);
	}

	@Autowired
	public ReviewService(UserDAO userDAO, RoleDAO roleDAO, ProgramDAO programmeDAO, ApplicationFormDAO applicationDAO, ReviewerDAO reviewerDAO, ReviewRoundDAO reviewRoundDAO) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.programmeDAO = programmeDAO;
		this.applicationDAO = applicationDAO;
		this.reviewerDAO = reviewerDAO;
		this.reviewRoundDAO = reviewRoundDAO;
	
	}
	
	@Transactional
	public void moveApplicationToReview(ApplicationForm application, ReviewRound reviewRound, RegisteredUser... reviewerUsers) {
		checkApplicationStatus(application);
		application.setLatestReviewRound(reviewRound);		
		for (RegisteredUser reviewerUser : reviewerUsers) {
			if (!reviewerUser.isInRole(Authority.REVIEWER)) {
				throw new IllegalStateException(//
						String.format("User '%s' is not a reviewer!", reviewerUser.getUsername()));
			}
			if (!reviewerUser.isReviewerInProgramme(application.getProgram())) {
				throw new IllegalStateException(//
						String.format("User '%s' is not a reviewer in programme '%s'!", reviewerUser.getUsername(), application.getProgram().getTitle()));
			}

			if (!reviewerUser.isReviewerInLatestReviewRoundOfApplicationForm(application)) {
				Reviewer reviewer = new Reviewer();
				reviewer.setUser(reviewerUser);				
				reviewRound.getReviewers().add(reviewer);				
			}
		}
		reviewRound.setApplication(application);
		reviewRoundDAO.save(reviewRound);
		
		application.setStatus(ApplicationFormStatus.REVIEW);
		applicationDAO.save(application);
	}


	

	

	/**
	 * Associates given user to the given programme.
	 */
	@Transactional
	public void addUserToProgramme(Program programme, RegisteredUser reviewer) {
		if (!reviewer.isInRole(Authority.REVIEWER)) {
			Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
			reviewer.getRoles().add(reviewerRole);
		}
		reviewer.getProgramsOfWhichReviewer().add(programme);
		programme.getProgramReviewers().add(reviewer);
		userDAO.save(reviewer);
		programmeDAO.save(programme);
	}


	private void checkApplicationStatus(ApplicationForm application) {
		ApplicationFormStatus status = application.getStatus();
		switch (status) {
		case VALIDATION:
		case REVIEW:
			break;
		default:
			throw new IllegalStateException(String.format("Application in invalid status: '%s'!", status));
		}
	}

}
