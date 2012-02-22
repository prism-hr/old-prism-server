package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;


public class UserDAOTest extends AutomaticRollbackTestCase {
	private UserDAO userDAO;

	@Test
	public void shouldSaveAndLoadUser() throws Exception {


		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();

		assertNull(user.getId());

		userDAO.save(user);

		assertNotNull(user.getId());
		Integer id = user.getId();
		RegisteredUser reloadedUser = userDAO.get(id);
		assertSame(user, reloadedUser);

		flushAndClearSession();

		reloadedUser = userDAO.get(id);
		assertNotSame(user, reloadedUser);
		assertEquals(user, reloadedUser);


	}

	@Test
	public void shouldFindUsersByUsername() throws Exception {


		RegisteredUser userOne = 	new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();
		RegisteredUser userTwo =	new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).toUser();

		save(userOne, userTwo);

		flushAndClearSession();		

		RegisteredUser foundUser = userDAO.getUserByUsername("username");		
		assertEquals(userOne, foundUser);

	}

	@Test
	public void shouldFindOneReviewer() {
		sessionFactory.getCurrentSession().createSQLQuery("delete from USER_ROLE_LINK").executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery("delete from APPLICATION_ROLE").executeUpdate();
		
		Role roleOne = new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole();
		Role roleTwo = new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole();
		save(roleOne, roleTwo);
		
		RegisteredUser userOne = 	new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).role(roleOne).toUser();
		
		RegisteredUser userTwo =	new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("otherusername").password("password").accountNonExpired(false).accountNonLocked(false)
		.credentialsNonExpired(false).enabled(false).role(roleTwo).toUser();
		
		save(userOne, userTwo);

		flushAndClearSession();		

		List<RegisteredUser> foundReviewers = userDAO.getReviewers();
		assertEquals(1, foundReviewers.size());
		assertTrue(foundReviewers.contains(userOne));
		assertFalse(foundReviewers.contains(userTwo));
	}

	@Before
	public void setup(){
		userDAO = new UserDAO(sessionFactory);

	}
}
