
package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

public class RegisterControllerTest {

	private RegisterController registerController;	
	private UserService userServiceMock;
	private ApplicantRecordValidator validatorMock;
	private RegistrationService registrationServiceMock;
	
	
	@Before
	public void setUp() {
		validatorMock = EasyMock.createMock(ApplicantRecordValidator.class);		
		userServiceMock = EasyMock.createMock(UserService.class);
		registrationServiceMock = EasyMock.createMock(RegistrationService.class);
		registerController = new RegisterController(validatorMock, userServiceMock, registrationServiceMock);
	}
	
	@Test
	public void shouldReturnRegisterPage() throws NoSuchAlgorithmException{
		ModelAndView modelAndView = registerController.getRegisterPage();
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
	}
	
	@Test
	public void shouldReturnToRegistrationPageIfErrors(){
		RegistrationDTO recordDTO = new RegistrationDTO();
		recordDTO.setFirstname("Mark");
		recordDTO.setLastname("Euston");
		recordDTO.setEmail("meuston@gmail.com");
		recordDTO.setPassword("1234");
		recordDTO.setConfirmPassword("1234");
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		validatorMock.validate(recordDTO, errorsMock);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		
		
		EasyMock.replay(validatorMock, errorsMock, registrationServiceMock);
		
		ModelAndView modelAndView = registerController.submitRegistration(recordDTO, errorsMock);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
		assertSame(recordDTO, ((RegisterPageModel) modelAndView.getModel().get("model")).getRecord());
		assertSame(errorsMock, ((RegisterPageModel) modelAndView.getModel().get("model")).getResult());
		EasyMock.verify(registrationServiceMock);
	}
	
	@Test
	public void shouldCreateAndSaveNewUserIfNoErrors(){
		RegistrationDTO recordDTO = new RegistrationDTO();
		recordDTO.setFirstname("Mark");
		recordDTO.setLastname("Euston");
		recordDTO.setEmail("meuston@gmail.com");
		recordDTO.setPassword("1234");
		recordDTO.setConfirmPassword("1234");
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		validatorMock.validate(recordDTO, errorsMock);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		registrationServiceMock.generateAndSaveNewUser(recordDTO);
		
		EasyMock.replay(validatorMock, errorsMock, registrationServiceMock);
		
		ModelAndView modelAndView = registerController.submitRegistration(recordDTO, errorsMock);
		assertEquals("redirect:/register/complete", modelAndView.getViewName());
		
		EasyMock.verify(registrationServiceMock);
	}
	

	
	
	@Test
	public void shouldActivateAccount(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).activationCode("ul5oaij68186jbcg").enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByUsername(user.getUsername())).andReturn(user);
		String activationCode = "ul5oaij68186jbcg";
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(user, activationCode);
		EasyMock.verify(userServiceMock);
		assertTrue(((RegisterPageModel)modelAndView.getModel().get("model")).getUser().isEnabled());
	}
	
	@Test
	public void shouldNotActivateAccount(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).activationCode("ul5oaij68186jbcg").enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByUsername(user.getUsername())).andReturn(user);
		String activationCode = "differentactivationcode";
		EasyMock.replay(userServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(user, activationCode);
		assertFalse(((RegisterPageModel)modelAndView.getModel().get("model")).getUser().isEnabled());
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
