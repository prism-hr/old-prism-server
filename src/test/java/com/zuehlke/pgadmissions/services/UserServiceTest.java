package com.zuehlke.pgadmissions.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;


public class UserServiceTest {

	private UserDAO userDAOMock;
	private RegisteredUser user;
	private UserService userService;
	
	@Test
	public void shouldgetListOfReviewersForApplication(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).username("tom").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userDAOMock);
		List<RegisteredUser> reviewersForApplication = userService.getReviewersForApplication(form);
		Assert.assertTrue(reviewersForApplication.contains(reviewer));
		Assert.assertEquals(1, reviewersForApplication.size());
	}
	
	@Test
	public void shouldgetEmptyListOfReviewersForApplication(){
		RegisteredUser reviewer = new RegisteredUserBuilder().id(1).username("tom").role(new RoleBuilder().authorityEnum(Authority.REVIEWER).toRole()).toUser();

		Set<RegisteredUser> reviewers = new HashSet<RegisteredUser>();
		reviewers.add(reviewer);
		
		ApplicationForm form = new ApplicationFormBuilder().id(1).reviewers(reviewers).toApplicationForm();
		EasyMock.expect(userDAOMock.getAllUsers()).andReturn(Arrays.asList(reviewer));
		EasyMock.replay(userDAOMock);
		
		List<RegisteredUser> reviewersForApplication = userService.getReviewersForApplication(form);
		
		Assert.assertFalse(reviewersForApplication.contains(reviewer));
		Assert.assertEquals(0, reviewersForApplication.size());
	}
	
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		userDAOMock = EasyMock.createMock(UserDAO.class);
		userService = new UserService(userDAOMock);
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
	
}
