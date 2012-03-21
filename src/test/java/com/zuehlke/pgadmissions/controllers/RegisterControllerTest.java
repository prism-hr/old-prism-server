
package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
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
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.ApplicantRecordValidator;

public class RegisterControllerTest {

	private RegisterController registerController;	
	private UserService userServiceMock;
	private ApplicantRecordValidator validatorMock;
	private RegistrationService registrationServiceMock;
	private ApplicationsService applicationsServiceMock;
	
	
	@Before
	public void setUp() {
		validatorMock = EasyMock.createMock(ApplicantRecordValidator.class);		
		userServiceMock = EasyMock.createMock(UserService.class);
		registrationServiceMock = EasyMock.createMock(RegistrationService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		registerController = new RegisterController(validatorMock, userServiceMock, registrationServiceMock, applicationsServiceMock);
	}
	
	@Test
	public void shouldReturnRegisterPage(){
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
		recordDTO.setProjectId(1);
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
		
		assertEquals("Sorry, the system was unable to process the activation request.", modelAndView.getModel().get("message"));
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
