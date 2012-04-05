
package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

public class RegisterControllerTest {

	private RegisterController registerController;	
	private UserService userServiceMock;
	private RegisterFormValidator validatorMock;
	private RegistrationService registrationServiceMock;
	private ApplicationsService applicationsServiceMock;
	
	
	@Before
	public void setUp() {
		validatorMock = EasyMock.createMock(RegisterFormValidator.class);		
		userServiceMock = EasyMock.createMock(UserService.class);
		registrationServiceMock = EasyMock.createMock(RegistrationService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		registerController = new RegisterController(validatorMock, userServiceMock, registrationServiceMock, applicationsServiceMock);
	}
	
	@Test
	public void shouldReturnRegisterPage(){
		ModelAndView modelAndView = registerController.getRegisterPage(null);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
	}
	
	@Ignore
	@Test
	public void shouldReturnToRegistrationPageIfErrors(){
		RegisteredUser record = new RegisteredUser();
		record.setFirstName("Mark");
		record.setLastName("Euston");
		record.setEmail("meuston@gmail.com");
		record.setPassword("1234");
		record.setConfirmPassword("1234");		
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		validatorMock.validate(record, errorsMock);
		validatorMock.shouldValidateSameEmail(true);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);
		
		
		EasyMock.replay(validatorMock, errorsMock, registrationServiceMock);
		
		ModelAndView modelAndView = registerController.submitRegistration(record, null, errorsMock);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
		assertSame(record, ((RegisterPageModel) modelAndView.getModel().get("model")).getRecord());
		assertSame(errorsMock, ((RegisterPageModel) modelAndView.getModel().get("model")).getResult());
		EasyMock.verify(registrationServiceMock);
	}
	@Ignore
	@Test
	public void shouldCreateAndSaveNewUserIfNoErrors(){
		RegisteredUser record = new RegisteredUser();
		record.setFirstName("Mark");
		record.setLastName("Euston");
		record.setEmail("emailofmarkeuston@gmail.com");
		record.setPassword("12345678");
		record.setConfirmPassword("12345678");
		record.setProjectId(1);
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		validatorMock.shouldValidateSameEmail(true);
		validatorMock.validate(record, errorsMock);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		registrationServiceMock.generateAndSaveNewUser(record, null);
		
		EasyMock.replay( registrationServiceMock);
		
		ModelAndView modelAndView = registerController.submitRegistration(record, null, errorsMock);
		assertEquals("redirect:/register/complete", modelAndView.getViewName());
		
		EasyMock.verify(registrationServiceMock);
	}
	

	
	
	@Test
	public void shouldActivateAccountAndRedirectToDefaultViewIfNoProject(){
		String activationCode = "ul5oaij68186jbcg";
		RegisteredUser user = new RegisteredUserBuilder().id(1).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(user);		
		userServiceMock.save(user);
		EasyMock.replay(registrationServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit( activationCode);		
		EasyMock.verify(registrationServiceMock);
		assertEquals("redirect:/applications", modelAndView.getViewName());
		assertTrue(user.isEnabled());
	}
	
	@Test
	public void shouldCreatNewApplicationAndRedirectToItIfUserHasOriginalProject(){
		String activationCode = "ul5oaij68186jbcg";
		Project project = new ProjectBuilder().id(1).toProject();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(1).projectOriginallyAppliedTo(project).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(user);		
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, project)).andReturn(applicationForm);
		EasyMock.replay(registrationServiceMock, applicationsServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(registrationServiceMock);
		assertEquals("redirect:/application?id=21", modelAndView.getViewName());		
		assertTrue(user.isEnabled());
	}
	
	@Test
	public void shouldReturnToRegistrationPageIfNouserFound(){
		
		String activationCode = "differentactivationcode";
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(null);		
		EasyMock.replay(registrationServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit( activationCode);
		assertEquals("public/register/register_info", modelAndView.getViewName());
		RegisterPageModel model = (RegisterPageModel) modelAndView.getModel().get("model");
		assertEquals("Sorry, the system was unable to process the activation request.", model.getMessage());
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
