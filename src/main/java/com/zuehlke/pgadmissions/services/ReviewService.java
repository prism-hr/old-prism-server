package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.ProgramDAO;
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
	private final ReviewerService reviewerService;

	ReviewService() {
		this(null, null, null, null, null);
	}

	@Autowired
	public ReviewService(UserDAO userDAO, RoleDAO roleDAO, ProgramDAO programmeDAO, ApplicationFormDAO applicationDAO, ReviewerService reviewerService) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.programmeDAO = programmeDAO;
		this.applicationDAO = applicationDAO;
		this.reviewerService = reviewerService;
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
				
			}
		}
		application.setStatus(ApplicationFormStatus.REVIEW);
		applicationDAO.save(application);
	}

	/**
	 * Creates a new user with role {@link Authority#REVIEWER} and associates it
	 * with the given programme.
	 * 
	 * @throws IllegalStateException
	 *             if user already exists.
	 */
	@Transactional
	public RegisteredUser createNewReviewerForProgramme(Program programme, String firstName, String lastName, String email) {
		RegisteredUser newUser = userDAO.getUserByEmail(email);
		if (newUser != null) {
			throw new IllegalStateException(String.format("user with email: %s already exists!", email));
		}
		newUser = createNewReviewer(firstName, lastName, email);
		newUser.getProgramsOfWhichReviewer().add(programme);
		programme.getProgramReviewers().add(newUser);
		userDAO.save(newUser);
		programmeDAO.save(programme);
		Reviewer reviewer = new Reviewer();
		reviewer.setUser(newUser);
		reviewerService.save(reviewer);
		return newUser;
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

	private RegisteredUser createNewReviewer(String firstName, String lastName, String email) {
		RegisteredUser user = new RegisteredUser();

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(email);
		user.setEmail(email);
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setEnabled(false);
		user.setCredentialsNonExpired(true);

		user.getRoles().add(roleDAO.getRoleByAuthority(Authority.REVIEWER));
		return user;
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
