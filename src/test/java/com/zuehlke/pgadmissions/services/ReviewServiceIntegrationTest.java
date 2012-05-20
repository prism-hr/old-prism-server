package com.zuehlke.pgadmissions.services;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ProgramDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integrationTestContext.xml")
public class ReviewServiceIntegrationTest {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ProgramDAO programDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private RoleDAO roleDAO;

	@Autowired
	private EncryptionUtils encryptionUtils;

	@Autowired
	private SessionFactory sessionFactory;


	

	private void flushNClear() {
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
	}

	private RegisteredUser createNewReviewerUser(String firstName, String lastName, String email, Authority authority) {
		RegisteredUser newReviewer = new RegisteredUser();
		newReviewer.setFirstName(firstName);
		newReviewer.setLastName(lastName);
		newReviewer.setActivationCode(encryptionUtils.generateUUID());
		newReviewer.setUsername(email);
		newReviewer.setEmail(email);
		newReviewer.setAccountNonExpired(true);
		newReviewer.setAccountNonLocked(true);
		newReviewer.setEnabled(false);
		newReviewer.setCredentialsNonExpired(true);
		if (authority != null) {
			newReviewer.getRoles().add(roleDAO.getRoleByAuthority(authority));
		}
		return newReviewer;
	}
}
