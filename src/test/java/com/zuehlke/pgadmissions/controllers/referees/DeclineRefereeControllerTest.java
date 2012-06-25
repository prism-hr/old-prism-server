package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;

public class DeclineRefereeControllerTest {

	private RefereeService refereeServiceMock;
	private DeclineRefereeController controller;
	private RegisteredUser currentUser;
	
	private EncryptionHelper encryptionHelper;
	private UserService userServiceMock;

	@Test
	public void shouldGetRefereeFromService() {
		Referee referee = new RefereeBuilder().id(1).user(currentUser).toReferee();
		EasyMock.expect(encryptionHelper.decryptToInteger("enc1")).andReturn(1);
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock, encryptionHelper);
		Referee returnedReferee = controller.getReferee("enc1");
		assertEquals(referee, returnedReferee);
		EasyMock.verify(refereeServiceMock, encryptionHelper);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfRefereeDoesNotExist() {
		EasyMock.expect(encryptionHelper.decryptToInteger("enc1")).andReturn(1);
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(null);
		EasyMock.replay(refereeServiceMock, encryptionHelper);
		controller.getReferee("enc1");
		
		EasyMock.verify(refereeServiceMock, encryptionHelper);
	}
	
	@Test(expected=ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfCurrentUserNotUserOfReferee() {
		Referee referee = new RefereeBuilder().id(1).toReferee();
		EasyMock.expect(encryptionHelper.decryptToInteger("enc1")).andReturn(1);
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		EasyMock.replay(refereeServiceMock, encryptionHelper);
		controller.getReferee("enc1");
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
		encryptionHelper = EasyMock.createMock(EncryptionHelper.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		controller = new DeclineRefereeController(refereeServiceMock, userServiceMock, encryptionHelper);

		currentUser = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUser);
		EasyMock.replay(userServiceMock);
		
	}
}
