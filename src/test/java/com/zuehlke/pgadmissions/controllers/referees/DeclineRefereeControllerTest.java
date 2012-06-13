package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.RefereeService;

public class DeclineRefereeControllerTest {

	private RefereeService refereeServiceMock;
	private DeclineRefereeController controller;
	private RegisteredUser currentUser;
	private UsernamePasswordAuthenticationToken authenticationToken;

	@Test
	public void shouldGetRefereeFromService() {
		Referee referee = new RefereeBuilder().id(1).user(currentUser).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		Referee returnedReferee = controller.getReferee(1);
		assertEquals(referee, returnedReferee);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfRefereeDoesNotExist() {
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(null);
		EasyMock.replay(refereeServiceMock);
		controller.getReferee(1);
		
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserNotUserOfReferee() {
		Referee referee = new RefereeBuilder().id(1).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		controller.getReferee(1);
	}
	
	@Test
	public void shouldSetDeclinedToTrueAndSaveReferee(){
		Referee referee = new RefereeBuilder().id(1).toReferee();		
		refereeServiceMock.declineToActAsRefereeAndNotifiyApplicant(referee);
		EasyMock.replay(refereeServiceMock);
		String view =  controller.decline(referee);
		EasyMock.verify(refereeServiceMock);
		assertEquals("private/referees/referee_declined", view);
		
	}

	@Before
	public void setup() {
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		controller = new DeclineRefereeController(refereeServiceMock);
		
		
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		currentUser = new RegisteredUserBuilder().id(1).toUser();
		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}
}
