package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
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
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.RefereePropertyEditor;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

public class RegisterRefereeControllerTest {

	private RegisterRefereeController registerRefereeController;
	private UserService userServiceMock;
	private RegisterFormValidator validator;
	private EncryptionUtils encryptionUtils;
	private RefereePropertyEditor refereePropertyEditorMock;
	private EncryptionHelper encryptionHelperMock;
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResoureNotFoundExceptionIfRefereeDoesNotMappedToApplicationReferee() {
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").lastName("l").email("email@test.com").password("12345678").confirmPassword("12345678").username("u").toUser();
		EasyMock.expect(encryptionHelperMock.decryptToInteger("alal")).andReturn(user.getId());
		EasyMock.replay(encryptionHelperMock);

		registerRefereeController.getReferee("alal");
	}
	
	@Test
	public void shouldGetRefereeFromServiceIfIdAndIsMappedToAnApplicationRefereeProvided() {
		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").referees(referee).lastName("l").email("e@test.com").username("u").toUser();
		EasyMock.expect(encryptionHelperMock.decryptToInteger("1")).andReturn(1);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		EasyMock.replay(userServiceMock, encryptionHelperMock);
		
		RegisteredUser returnedUser = registerRefereeController.getReferee("1");

		EasyMock.verify(userServiceMock, encryptionHelperMock);
		assertEquals(user, returnedUser);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResoureNotFoundExceptionIfRefereeDoesNotExists() {
		ApplicationForm application = new ApplicationFormBuilder().status(ApplicationFormStatus.VALIDATION).id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().firstname("f").lastname("l").email("e@test.com").application(application).toReferee();
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("f").referees(referee).lastName("l").email("e@test.com").username("u").toUser();
		EasyMock.expect(encryptionHelperMock.decryptToInteger("1")).andReturn(user.getId());
		EasyMock.replay(encryptionHelperMock);
		registerRefereeController.getReferee("1");
	}
	
	@Test
	public void shouldSaveRefereeIfValid(){
		ModelMap modelMap = new ModelMap();
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).email("email").firstName("first").username("email").lastName("last").confirmPassword("12345678").password("12345678").toUser();
		userServiceMock.saveAndEmailRegisterConfirmationToReferee(refereeUser);
		EasyMock.replay(userServiceMock);
		BindingResult errors = EasyMock.createMock(BindingResult.class);		
		registerRefereeController.submitRefereeAndGetLoginPage(refereeUser,  errors, modelMap);
		EasyMock.verify(userServiceMock);
	}
	
	
	@Before
	public void setUp(){
		userServiceMock = EasyMock.createMock(UserService.class);
		validator = EasyMock.createMock(RegisterFormValidator.class);
		encryptionUtils = EasyMock.createMock(EncryptionUtils.class);
		refereePropertyEditorMock = EasyMock.createMock(RefereePropertyEditor.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		registerRefereeController = new RegisterRefereeController(userServiceMock, validator, encryptionUtils, refereePropertyEditorMock, encryptionHelperMock);
		
	}
}
