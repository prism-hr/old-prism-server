package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
public class ReviewService {

	private final RoleDAO roleDAO;
	private final UserDAO userDAO;

	ReviewService() {
		this(null, null);
	}

	@Autowired
	public ReviewService(UserDAO userDAO, RoleDAO roleDAO) {
		this.userDAO = userDAO;
		this.roleDAO = roleDAO;
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
		return newUser;
	}

	/**
	 * Associates given user to the given programme.
	 */
	@Transactional
	public void addUserToProgramme(Program programme, RegisteredUser reviewer) {
		if( programme.getReviewers().contains(reviewer)) {
			return;
		}
		if( !reviewer.isInRole(Authority.REVIEWER)) {
			Role reviewerRole = roleDAO.getRoleByAuthority(Authority.REVIEWER);
			reviewer.getRoles().add(reviewerRole);
		}
		reviewer.getProgramsOfWhichReviewer().add(programme);
		programme.getReviewers().add(reviewer);
		userDAO.save(reviewer);
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
}
