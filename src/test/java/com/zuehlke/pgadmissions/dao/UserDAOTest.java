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
	

		RegisteredUser user = new RegisteredUser();
		user.setPassword("password");
		user.setUsername("username");

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
		assertEquals(user.getPassword(), reloadedUser.getPassword());
		assertEquals(user.getUsername(), reloadedUser.getUsername());		

	}
	
	@Test
	public void shouldFindUsersByUsername() throws Exception {
	

		RegisteredUser userOne = new RegisteredUserBuilder().username("username").password("password").toUser();
		RegisteredUser userTwo = new RegisteredUserBuilder().username("otherusername").password("password").toUser();

		save(userOne, userTwo);
		
		flushAndClearSession();		
	
		RegisteredUser foundUser = userDAO.getUserByUsername("username");		
		assertEquals(userOne, foundUser);
		assertEquals(userOne.getPassword(), foundUser.getPassword());
		assertEquals(userOne.getUsername(), foundUser.getUsername());		

	}
	
	@Before
	public void setup(){
		userDAO = new UserDAO(sessionFactory);
		
	}
}
