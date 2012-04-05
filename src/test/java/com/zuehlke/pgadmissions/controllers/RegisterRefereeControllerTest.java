package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

public class RegisterRefereeControllerTest {

	private RegisterRefereeController registerRefereeController;
	private UserService userServiceMock;
	private RefereeService refereeServiceMock;
	private ApplicantRecordValidator validator;
	private EncryptionUtils encryptionUtils;
	
	@Test(expected=CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormAlreadySubmitted() {
		ApplicationForm application = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().firstName("f").referee(referee).lastName("l").email("e@test.com").username("u").toUser();
		BindingResult errors = EasyMock.createMock(BindingResult.class);		
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock, errors);
		registerRefereeController.submitRefereeAndGetLoginPage(user, errors);
		EasyMock.verify(userServiceMock);
	}
	
	@Test
	public void shouldSaveRefereeAndEnableAccountIfNoErrors() {
		ApplicationForm application = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().firstName("f").referee(referee).lastName("l").email("e@test.com").password("123").username("u").toUser();
		BindingResult errors = EasyMock.createMock(BindingResult.class);		
		
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResoureNotFoundExceptionIfRefereeDoesNotMappedToApplicationReferee() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").lastName("l").email("e@test.com").username("u").toUser();
		
		registerRefereeController.getReferee(user.getId());
	}
	
	@Test
	public void shouldGetRefereeFromServiceIfIdAndIsMappedToAnApplicationRefereeProvided() {
		ApplicationForm application = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").referee(referee).lastName("l").email("e@test.com").username("u").toUser();
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.replay(userServiceMock);
		RegisteredUser returnedUser = registerRefereeController.getReferee(1);
		assertEquals(user, returnedUser);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResoureNotFoundExceptionIfRefereeDoesNotExists() {
		ApplicationForm application = new ApplicationFormBuilder().submissionStatus(SubmissionStatus.SUBMITTED).id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").referee(referee).lastName("l").email("e@test.com").username("u").toUser();
		registerRefereeController.getReferee(user.getId());
	}
	
	@Before
	public void setUp(){
		userServiceMock = EasyMock.createMock(UserService.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		validator = EasyMock.createMock(ApplicantRecordValidator.class);
		encryptionUtils = EasyMock.createMock(EncryptionUtils.class);
		registerRefereeController = new RegisterRefereeController(userServiceMock, refereeServiceMock, validator, encryptionUtils);
		
	}
}
