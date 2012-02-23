package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;


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

	@Before
	public void setup(){
		userDAO = new UserDAO(sessionFactory);

	}
}
