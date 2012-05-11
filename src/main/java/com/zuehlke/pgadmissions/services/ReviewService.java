package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.ReviewerDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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
	

	ReviewService() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ReviewService(UserDAO userDAO, RoleDAO roleDAO, ProgramDAO programmeDAO, ApplicationFormDAO applicationDAO, ReviewerDAO reviewerDAO) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.programmeDAO = programmeDAO;
		this.applicationDAO = applicationDAO;
		this.reviewerDAO = reviewerDAO;
	
	}

	/**
	 * Associates the given reviewers to the application and updates the
	 * application record(s) if necessary. Silently handles reviewers already
	 * associated with the application, but throws {@link IllegalStateException}
	 * s when the users don't have the role reviewer or are not reviewer of the
	 * programme of the application.
	 * 
	 * @param application
	 * @param reviewerUsers
	 */
	@Transactional
	public void moveApplicationToReview(ApplicationForm application, RegisteredUser... reviewerUsers) {
		checkApplicationStatus(application);
		Program programme = application.getProgram();
		for (RegisteredUser reviewerUser : reviewerUsers) {
			if (!reviewerUser.isInRole(Authority.REVIEWER)) {
				throw new IllegalStateException(//
						String.format("User '%s' is not a reviewer!", reviewerUser.getUsername()));
			}
			if (!reviewerUser.isReviewerInProgramme(programme)) {
				throw new IllegalStateException(//
						String.format("User '%s' is not a reviewer in programme '%s'!", reviewerUser.getUsername(), programme.getTitle()));
			}

			if (!reviewerUser.isReviewerOfApplicationForm(application)) {
				Reviewer reviewer = new Reviewer();
				reviewer.setUser(reviewerUser);
				reviewer.setApplication(application);
				application.getReviewers().add(reviewer);
				reviewerDAO.save(reviewer);
			}
		}
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
