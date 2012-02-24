package com.zuehlke.pgadmissions.dwr;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dwr.models.PersonalDetailsDWR;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;


public class ApplicationManagementDWRServiceTest {
	
	private RegisteredUser user;
	private ApplicationManagementDWRService dwrService; 
	private UserService userServiceMock;
	private ApplicationsService applicationsServiceMock;

	@Test
	public void shouldDisplayPersonalDetails() {
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.replay(userServiceMock);
		
		PersonalDetailsDWR displayPersonalDetails = dwrService.displayPersonalDetails(1);
		Assert.assertEquals("Jack", displayPersonalDetails.getFirstName());
		Assert.assertEquals("Johnson", displayPersonalDetails.getLastName());
	}
	
	@Before
	public void setUp(){
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").firstName("Jack").lastName("Johnson").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
		
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		
		dwrService = new ApplicationManagementDWRService(applicationsServiceMock, userServiceMock);
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
