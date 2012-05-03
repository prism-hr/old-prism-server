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
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
public class ReviewService {

	private final RoleDAO roleDAO;
	private final UserDAO userDAO;
	private final ProgramDAO programmeDAO;
	private final ApplicationFormDAO applicationDAO;

	ReviewService() {
		this(null, null, null, null);
	}

	@Autowired
	public ReviewService(UserDAO userDAO, RoleDAO roleDAO, ProgramDAO programmeDAO, ApplicationFormDAO applicationDAO) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
		this.programmeDAO = programmeDAO;
		this.applicationDAO = applicationDAO;
	}

	/**
	 * Associates the given reviewers to the application and updates the application record(s) if
	 * necessary. Silently handles reviewers already associated with the application, but 
	 * throws {@link IllegalStateException}s when the users don't have the role reviewer or
	 * are not reviewer of the programme of the application.
	 * @param application
	 * @param reviewers
	 */
	@Transactional
	public void moveApplicationToReview(ApplicationForm application, RegisteredUser... reviewers) {
		checkApplicationStatus(application);

		List<RegisteredUser> applicationReviewers = application.getReviewers();
		Program programme = application.getProgram();
		for (RegisteredUser reviewer : reviewers) {
			if (!reviewer.isInRole(Authority.REVIEWER)) {
				throw new IllegalStateException(//
						String.format("User '%s' is not a reviewer!", reviewer.getUsername()));
			}
			if (!reviewer.isReviewerInProgramme(programme)) {
				throw new IllegalStateException(//
						String.format("User '%s' is not a reviewer in programme '%s'!", reviewer.getUsername(), programme.getTitle()));
			}

			if (!applicationReviewers.contains(reviewer)) {
				applicationReviewers.add(reviewer);
			}
		}
		application.setStatus(ApplicationFormStatus.REVIEW);
		applicationDAO.save(application);
	}

	/**
	 * Creates a new user with role {@link Authority#REVIEWER} and associates it with the given programme.
	 * 
	 * @throws IllegalStateException if user already exists.
	 */
	@Transactional
	public RegisteredUser createNewReviewerForProgramme(Program programme, String firstName, String lastName, String email) {
		RegisteredUser newUser = userDAO.getUserByEmail(email);
		if (newUser != null) {
			throw new IllegalStateException(String.format("user with email: %s already exists!", email));
		}
		newUser = createNewReviewer(firstName, lastName, email);
		newUser.getProgramsOfWhichReviewer().add(programme);
		programme.getReviewers().add(newUser);
		userDAO.save(newUser);
		programmeDAO.save(programme);
		return newUser;
	}

	/**
	 * Associates given user to the given programme.
	 */
	@Transactional
	public void addUserToProgramme(Program programme, RegisteredUser reviewer) {
		if (programme.getReviewers().contains(reviewer)) {
			return;
		}
		if (!reviewer.isInRole(Authority.REVIEWER)) {
			Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
			reviewer.getRoles().add(reviewerRole);
		}
		reviewer.getProgramsOfWhichReviewer().add(programme);
		programme.getReviewers().add(reviewer);
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
