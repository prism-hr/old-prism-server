package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.RefereePropertyEditor;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

public class RegisterRefereeControllerTest {

	private RegisterRefereeController registerRefereeController;
	private UserService userServiceMock;
	private RefereeService refereeServiceMock;
	private RegisterFormValidator validator;
	private EncryptionUtils encryptionUtils;
	private RefereePropertyEditor refereePropertyEditorMock;
	
	@Test
	
	public void shouldSaveRefereeAndEnableAccountIfNoErrors() {
	/*	ApplicationForm application = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().firstName("f").referees(referee).lastName("l").email("e@test.com").password("123").username("u").toUser();
		BindingResult errors = EasyMock.createMock(BindingResult.class);		*/
		
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResoureNotFoundExceptionIfRefereeDoesNotMappedToApplicationReferee() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").lastName("l").email("email@test.com").password("12345678").confirmPassword("12345678").username("u").toUser();
		
		registerRefereeController.getReferee(user.getId());
	}
	
	@Test
	public void shouldGetRefereeFromServiceIfIdAndIsMappedToAnApplicationRefereeProvided() {
		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").referees(referee).lastName("l").email("e@test.com").username("u").toUser();
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.replay(userServiceMock);
		RegisteredUser returnedUser = registerRefereeController.getReferee(1);
		assertEquals(user, returnedUser);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResoureNotFoundExceptionIfRefereeDoesNotExists() {
		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").referees(referee).lastName("l").email("e@test.com").username("u").toUser();
		registerRefereeController.getReferee(user.getId());
	}
	
	@Test
	public void shouldSaveRefereeIfValid(){
		ModelMap modelMap = new ModelMap();
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).email("email").firstName("first").username("email").lastName("last").confirmPassword("12345678").password("12345678").toUser();
		Referee referee = new RefereeBuilder().id(1).toReferee();
		userServiceMock.saveAndEmailRegisterConfirmationToReferee(refereeUser);
		EasyMock.replay(userServiceMock);
		BindingResult errors = EasyMock.createMock(BindingResult.class);		
		registerRefereeController.submitRefereeAndGetLoginPage(refereeUser,  errors, modelMap);
		EasyMock.verify(userServiceMock);
	}
	
	
	@Before
	public void setUp(){
		userServiceMock = EasyMock.createMock(UserService.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		validator = EasyMock.createMock(RegisterFormValidator.class);
		encryptionUtils = EasyMock.createMock(EncryptionUtils.class);
		refereePropertyEditorMock = EasyMock.createMock(RefereePropertyEditor.class);
		registerRefereeController = new RegisterRefereeController(userServiceMock, refereeServiceMock, validator, encryptionUtils, refereePropertyEditorMock);
		
	}
}
