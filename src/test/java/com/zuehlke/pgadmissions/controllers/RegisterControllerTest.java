
package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.pagemodels.RegisterPageModel;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.ApplicationQueryStringParser;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

public class RegisterControllerTest {

	private RegisterController registerController;	
	private UserService userServiceMock;
	private RegisterFormValidator validatorMock;
	private RegistrationService registrationServiceMock;
	private ApplicationsService applicationsServiceMock;
	private ProgramsService programServiceMock;
	private ApplicationQueryStringParser qureyStringParserMock;
	private EncryptionHelper encryptionHelper;
	
	
	@Before
	public void setUp() {
		validatorMock = EasyMock.createMock(RegisterFormValidator.class);		
		userServiceMock = EasyMock.createMock(UserService.class);
		registrationServiceMock = EasyMock.createMock(RegistrationService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		programServiceMock = EasyMock.createMock(ProgramsService.class);
		qureyStringParserMock = EasyMock.createMock(ApplicationQueryStringParser.class);
		encryptionHelper = EasyMock.createMock(EncryptionHelper.class);

		registerController = new RegisterController(validatorMock, userServiceMock, registrationServiceMock, applicationsServiceMock, programServiceMock,qureyStringParserMock, encryptionHelper);
	}
	
	@Test
	public void shouldReturnRegisterPage(){
		ModelAndView modelAndView = registerController.getRegisterPage(null);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
	}
	
	
	@Test
	public void shouldRedirectToDirectURLIfUserExistsIsEnabledAndHasADirectURL(){
		RegisteredUser user = new RegisteredUserBuilder().enabled(true).id(1).directURL("/directHere").toUser();
		EasyMock.expect(userServiceMock.getUser(user.getId())).andReturn(user);
		EasyMock.replay(userServiceMock);
		ModelAndView modelAndView = registerController.getRegisterPage(user.getId());
		EasyMock.verify(userServiceMock);
		assertEquals("redirect:/directHere", modelAndView.getViewName());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundIfUserDoesNotExists(){
		ModelAndView modelAndView = registerController.getRegisterPage(1);
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
	}
	
	
	@Test
	public void shouldReturnToRegistrationPageIfErrors(){
		RegisteredUser record = new RegisteredUser();
		record.setId(4);
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
		
		ModelAndView modelAndView = registerController.submitRegistration(new MockHttpServletRequest(), record, null, errorsMock, new ModelMap());
		assertEquals("public/register/register_applicant", modelAndView.getViewName());
		assertSame(record, ((RegisterPageModel) modelAndView.getModel().get("model")).getRecord());
		assertSame(errorsMock, ((RegisterPageModel) modelAndView.getModel().get("model")).getResult());
		EasyMock.verify(registrationServiceMock);
	}
	
	@Test
	public void shouldResendConfirmationEmail(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).firstName("mark").lastName("euston").password("123").confirmPassword("123").toUser();
		EasyMock.expect(encryptionHelper.decryptToInteger("enc")).andReturn(1);
		EasyMock.expect(userServiceMock.getUser(1)).andReturn(user);
		registrationServiceMock.sendConfirmationEmail(user);
		EasyMock.replay(userServiceMock, registrationServiceMock, encryptionHelper);

		registerController.resendConfirmation("enc", new ModelMap());
		
		EasyMock.verify(userServiceMock, registrationServiceMock, encryptionHelper);
	}
	
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourveNotFoundIfUserIsNotFound(){
		EasyMock.expect(encryptionHelper.decryptToInteger("enc")).andReturn(2);
		EasyMock.expect(userServiceMock.getUser(2)).andReturn(null);
		EasyMock.replay(userServiceMock, encryptionHelper);
		registerController.resendConfirmation("enc", new ModelMap());
		EasyMock.verify(userServiceMock);
	}
	
	
	@Test
	public void shouldCreateAndSaveNewUserIfNoErrors(){
		RegisteredUser record = new RegisteredUser();
		record.setId(9);
		record.setFirstName("Mark");
		record.setLastName("Euston");
		record.setEmail("emailofmarkeuston@gmail.com");
		record.setPassword("12345678");
		record.setConfirmPassword("12345678");
		
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		validatorMock.shouldValidateSameEmail(true);
		validatorMock.validate(record, errorsMock);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		registrationServiceMock.generateAndSaveNewUser(record, null, null);
		
		EasyMock.replay( registrationServiceMock);
		
		ModelAndView modelAndView = registerController.submitRegistration(new MockHttpServletRequest(),record, null, errorsMock, new ModelMap());
		assertEquals("public/register/registration_complete", modelAndView.getViewName());
		
		EasyMock.verify(registrationServiceMock);
	}
	
	@Test
	public void shouldgetQueryStringFromSessionAndSetOnUserIfAvaialbe(){
		String queryString = "queryString";
		RegisteredUser record = new RegisteredUser();
		record.setId(9);
		record.setFirstName("Mark");
		record.setLastName("Euston");
		record.setEmail("emailofmarkeuston@gmail.com");
		record.setPassword("12345678");
		record.setConfirmPassword("12345678");
		
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		validatorMock.shouldValidateSameEmail(true);
		validatorMock.validate(record, errorsMock);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		registrationServiceMock.generateAndSaveNewUser(record, null,queryString);
		
		EasyMock.replay( registrationServiceMock);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("applyRequest", queryString);
		request.setSession(session);
		ModelAndView modelAndView = registerController.submitRegistration(request, record, null, errorsMock, new ModelMap());
		assertEquals("public/register/registration_complete", modelAndView.getViewName());
		
		EasyMock.verify(registrationServiceMock);
	}
	
	@Test
	public void shouldActivateAccountAndRedirectToApplicationListIfNoDirectURL() throws ParseException{
		String activationCode = "ul5oaij68186jbcg";
		RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(1).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(user);		
		userServiceMock.save(user);
		EasyMock.replay(registrationServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(registrationServiceMock);
		assertEquals("redirect:/applications", modelAndView.getViewName());
		assertTrue(user.isEnabled());
	}
	
	
	@Test
	public void shouldActivateAccountAndRedirectToDirectURLIfProvided() throws ParseException{
		String activationCode = "ul5oaij68186jbcg";
		RegisteredUser user = new RegisteredUserBuilder().directURL("/directLink").role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(1).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(user);		
		userServiceMock.save(user);
		EasyMock.replay(registrationServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(registrationServiceMock);
		assertEquals("redirect:/directLink", modelAndView.getViewName());
		assertTrue(user.isEnabled());
	}
	
	
	
	@Test
	public void shouldCreatNewApplicationAndRedirectToItIfQueryStringExistsOnUser() throws ParseException{		
		String activationCode = "ul5oaij68186jbcg";
		String queryString = "queryString";
		Date batchDeadline = new SimpleDateFormat("dd-MMM-yyyy").parse("01-Mar-2012");
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(user);
		EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(new String[]{"code", "http://www.url.com", "01-Mar-2012", "project title"});
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, program, batchDeadline, "project title", "http://www.url.com")).andReturn(applicationForm);
		EasyMock.replay(registrationServiceMock, applicationsServiceMock,programServiceMock,  qureyStringParserMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(registrationServiceMock);
		assertEquals("redirect:/application?applicationId=ABC", modelAndView.getViewName());		
		assertTrue(user.isEnabled());
	}
	
	@Test
	public void shouldNotFailIfDateNotCorrectlyFormattesd() throws ParseException{		
		String activationCode = "ul5oaij68186jbcg";
		String queryString = "queryString";
		
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(user);
		EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(new String[]{"code", "http://www.url.com", "bob", "project title"});
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, program, null, "project title", "http://www.url.com")).andReturn(applicationForm);
		EasyMock.replay(registrationServiceMock, applicationsServiceMock,programServiceMock,  qureyStringParserMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(registrationServiceMock);
		assertEquals("redirect:/application?applicationId=ABC", modelAndView.getViewName());		
		assertTrue(user.isEnabled());
	}
	@Test
	public void shouldNotFailIfUrlNotValid() throws ParseException{		
		String activationCode = "ul5oaij68186jbcg";
		String queryString = "queryString";
		
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(user);
		EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(new String[]{"code", "http://bob", null, "project title"});
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, program, null, "project title", null)).andReturn(applicationForm);
		EasyMock.replay(registrationServiceMock, applicationsServiceMock,programServiceMock,  qureyStringParserMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(registrationServiceMock);
		assertEquals("redirect:/application?applicationId=ABC", modelAndView.getViewName());		
		assertTrue(user.isEnabled());
	}
	
	@Test
	public void shouldNotFailIfOptionalParametersAreNulls() throws ParseException{		
		String activationCode = "ul5oaij68186jbcg";
		String queryString = "queryString";
		
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(user);
		EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(new String[]{"code",  null, null,  null});
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, program, null,null,  null)).andReturn(applicationForm);
		EasyMock.replay(registrationServiceMock, applicationsServiceMock,programServiceMock,  qureyStringParserMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(registrationServiceMock);
		assertEquals("redirect:/application?applicationId=ABC", modelAndView.getViewName());		
		assertTrue(user.isEnabled());
	}
	@Test
	public void shouldReturnToRegistrationPageIfNouserFound() throws ParseException{
		
		String activationCode = "differentactivationcode";
		EasyMock.expect(registrationServiceMock.findUserForActivationCode(activationCode)).andReturn(null);		
		EasyMock.replay(registrationServiceMock);
		ModelAndView modelAndView = registerController.activateAccountSubmit(activationCode);
		assertEquals("public/register/register_info", modelAndView.getViewName());
		RegisterPageModel model = (RegisterPageModel) modelAndView.getModel().get("model");
		assertEquals("Sorry, the system was unable to process the activation request.", model.getMessage());
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
