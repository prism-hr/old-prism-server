package com.zuehlke.pgadmissions.security;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.User;

public class PgAdmissionUserDetailsServiceTest {

	@Test
	public void shouldFindUserByUsername(){
		UserDAO userDAOMock = EasyMock.createMock(UserDAO.class);
		User user = new User().withId(1);
		
		String username = "username";
		EasyMock.expect(userDAOMock.getUserByEmail(username)).andReturn(user).anyTimes();
		EasyMock.replay(userDAOMock);
		
		PgAdmissionUserDetailsService service = new PgAdmissionUserDetailsService(userDAOMock);
		
		UserDetails userDetails = service.loadUserByUsername(username);
		assertEquals(userDetails, user);
		
	}
	
	@Test(expected=UsernameNotFoundException.class)
	public void shouldThrowUsernameNotFoundExceptionIfUserDoesNotExist(){
		UserDAO userDAOMock = EasyMock.createMock(UserDAO.class);		
		
		String username = "username";
		EasyMock.expect(userDAOMock.getUserByEmail(username)).andReturn(null).anyTimes();
		EasyMock.replay(userDAOMock);
		
		PgAdmissionUserDetailsService service = new PgAdmissionUserDetailsService(userDAOMock);
		
		service.loadUserByUsername(username);	
	}
}
