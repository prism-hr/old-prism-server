
package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.*;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Disability;
import com.zuehlke.pgadmissions.domain.Ethnicity;
import com.zuehlke.pgadmissions.domain.Language;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.RegistrationService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.ApplicationQueryStringParser;
import com.zuehlke.pgadmissions.validators.RegisterFormValidator;

public class RegisterControllerTest {

	private RegisterController registerController;	
	private UserService userServiceMock;
	private RegisterFormValidator regusterFormValidatorMock;
	private RegistrationService registrationServiceMock;
	private ApplicationsService applicationsServiceMock;
	private ProgramsService programServiceMock;
	private ApplicationQueryStringParser qureyStringParserMock;
	private EncryptionHelper encryptionHelper;
	
	
	@Before
	public void setUp() {
		regusterFormValidatorMock = EasyMock.createMock(RegisterFormValidator.class);		
		userServiceMock = EasyMock.createMock(UserService.class);
		registrationServiceMock = EasyMock.createMock(RegistrationService.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		programServiceMock = EasyMock.createMock(ProgramsService.class);
		qureyStringParserMock = EasyMock.createMock(ApplicationQueryStringParser.class);
		encryptionHelper = EasyMock.createMock(EncryptionHelper.class);

		registerController = new RegisterController(regusterFormValidatorMock, userServiceMock, registrationServiceMock, applicationsServiceMock, programServiceMock,qureyStringParserMock, encryptionHelper);
	}
	@Test
	public void shouldRegisterValidator(){
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(regusterFormValidatorMock);		
		EasyMock.replay(binderMock);
		registerController.registerValidator(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldReturnRegisterPage(){
		assertEquals("public/register/register_applicant",registerController.getRegisterPage(new RegisteredUserBuilder().enabled(true).id(1).toUser()));
	}
	
	
	@Test
	public void shouldRedirectToDirectURLIfUserExistsIsEnabledAndHasADirectURL(){
		assertEquals("redirect:/directHere",registerController.getRegisterPage(new RegisteredUserBuilder().enabled(true).id(1).directURL("/directHere").toUser()));
	}
	
	@Test
	public void shouldFindPendingUserByActivationCode(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();
		EasyMock.expect(userServiceMock.getUserByActivationCode("Abc")).andReturn(user);
		EasyMock.replay(userServiceMock);
		assertEquals(user, registerController.getPendingUser("Abc"));
		
	}
		
	@Test
	public void shouldReturnNewUserIfBlankActivationCode(){
		RegisteredUser pendingUser = registerController.getPendingUser("");
		assertNull( pendingUser.getId());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundIfUserDoesNotExists(){		
		EasyMock.expect(userServiceMock.getUserByActivationCode("Abc")).andReturn(null);
		EasyMock.replay(userServiceMock);
		registerController.getPendingUser("Abc");	
	}
	
	
	@Test
	public void shouldReturnToRegistrationPageIfErrors(){
		RegisteredUser pendingUser = new RegisteredUserBuilder().id(4).toUser();	
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);		
		EasyMock.replay( errorsMock, registrationServiceMock );
		
		String modelAndView = registerController.submitRegistration( pendingUser,  errorsMock, new MockHttpServletRequest());
		assertEquals("public/register/register_applicant", modelAndView);
	
		EasyMock.verify(registrationServiceMock);
	}
	
	
	@Test
	public void shouldCreateAndSaveNewUserIfNoErrors(){
		RegisteredUser pendingUser = new RegisteredUserBuilder().id(1).toUser();		
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);		
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		registrationServiceMock.updateOrSaveUser(pendingUser, null);
		
		EasyMock.replay( registrationServiceMock);
		
		String view = registerController.submitRegistration(pendingUser, errorsMock,new MockHttpServletRequest());
		assertEquals("public/register/registration_complete",view);
		
		EasyMock.verify(registrationServiceMock);
	}
	
	@Test
	public void shouldGetQueryStringFromSessionAndSetOnUserIfAvailable(){
		String queryString = "queryString";
		RegisteredUser pendingUser = new RegisteredUserBuilder().id(1).toUser();		
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);		
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
			
		registrationServiceMock.updateOrSaveUser(pendingUser, queryString);		
		EasyMock.replay( registrationServiceMock);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("applyRequest", queryString);
		request.setSession(session);
		String view =  registerController.submitRegistration(pendingUser, errorsMock,request);
		assertEquals("public/register/registration_complete",view);
		
		EasyMock.verify(registrationServiceMock);
	}
	
	@Test
	public void shouldResendConfirmationEmail(){
		RegisteredUser user = new RegisteredUserBuilder().id(1).toUser();

		EasyMock.expect(userServiceMock.getUserByActivationCode("abc")).andReturn(user);
		registrationServiceMock.sendConfirmationEmail(user);
		EasyMock.replay(userServiceMock, registrationServiceMock);

		String view = registerController.resendConfirmation("abc");
		
		EasyMock.verify(userServiceMock, registrationServiceMock);
		assertEquals("public/register/registration_complete", view);
	}
	
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourveNotFoundIfUserIsNotFound(){
		EasyMock.expect(userServiceMock.getUserByActivationCode("abc")).andReturn(null);
		EasyMock.replay(userServiceMock);
		registerController.resendConfirmation("abc");

	}
	

	@Test
	public void shouldActivateAccountAndRedirectToApplicationListIfNoDirectURL() throws ParseException{
		String activationCode = "ul5oaij68186jbcg";
		RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(1).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);		
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		String view = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(userServiceMock);
		assertEquals("redirect:/applications", view);
		assertTrue(user.isEnabled());
	}
	
	
	@Test
	public void shouldActivateAccountAndRedirectToDirectURLIfProvided() throws ParseException{
		String activationCode = "ul5oaij68186jbcg";
		RegisteredUser user = new RegisteredUserBuilder().directURL("/directLink").role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).id(1).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);		
		userServiceMock.save(user);
		EasyMock.replay(userServiceMock);
		String view = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(userServiceMock);
		assertEquals("redirect:/directLink",view);
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
		EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
		EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(new String[]{"code", "http://www.url.com", "01-Mar-2012", "project title"});
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, program, batchDeadline, "project title", "http://www.url.com")).andReturn(applicationForm);
		EasyMock.replay(userServiceMock, applicationsServiceMock,programServiceMock,  qureyStringParserMock);
		String view =   registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(userServiceMock);
		assertEquals("redirect:/application?applicationId=ABC", view);		
		assertTrue(user.isEnabled());
	}
	
	@Test
	public void shouldNotFailIfDateNotCorrectlyFormattesd() throws ParseException{		
		String activationCode = "ul5oaij68186jbcg";
		String queryString = "queryString";
		
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
		EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(new String[]{"code", "http://www.url.com", "bob", "project title"});
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, program, null, "project title", "http://www.url.com")).andReturn(applicationForm);
		EasyMock.replay(userServiceMock, applicationsServiceMock,programServiceMock,  qureyStringParserMock);
		String view = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(userServiceMock);
		assertEquals("redirect:/application?applicationId=ABC",view);		
		assertTrue(user.isEnabled());
	}
	@Test
	public void shouldNotFailIfUrlNotValid() throws ParseException{		
		String activationCode = "ul5oaij68186jbcg";
		String queryString = "queryString";
		
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
		EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(new String[]{"code", "http://bob", null, "project title"});
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, program, null, "project title", null)).andReturn(applicationForm);
		EasyMock.replay(userServiceMock, applicationsServiceMock,programServiceMock,  qureyStringParserMock);
		String view = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(userServiceMock);
		assertEquals("redirect:/application?applicationId=ABC",view);		
		assertTrue(user.isEnabled());
	}
	
	@Test
	public void shouldNotFailIfOptionalParametersAreNulls() throws ParseException{		
		String activationCode = "ul5oaij68186jbcg";
		String queryString = "queryString";
		
		Program program = new ProgramBuilder().id(1).toProgram();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(21).applicationNumber("ABC").toApplicationForm();
		RegisteredUser user = new RegisteredUserBuilder().id(1).originalApplicationQueryString(queryString).activationCode(activationCode).enabled(false).username("email@email.com").email("email@email.com").password("1234").toUser();
		EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(user);
		EasyMock.expect(qureyStringParserMock.parse(queryString)).andReturn(new String[]{"code",  null, null,  null});
		EasyMock.expect(programServiceMock.getProgramByCode("code")).andReturn(program);
		userServiceMock.save(user);
		EasyMock.expect(applicationsServiceMock.createAndSaveNewApplicationForm(user, program, null,null,  null)).andReturn(applicationForm);
		EasyMock.replay(userServiceMock, applicationsServiceMock,programServiceMock,  qureyStringParserMock);
		String view = registerController.activateAccountSubmit(activationCode);		
		EasyMock.verify(userServiceMock);
		assertEquals("redirect:/application?applicationId=ABC",view);		
		assertTrue(user.isEnabled());
	}
	@Test
	public void shouldReturnToRegistrationPageIfNouserFound() throws ParseException{
		
		String activationCode = "differentactivationcode";
		EasyMock.expect(userServiceMock.getUserByActivationCode(activationCode)).andReturn(null);		
		EasyMock.replay(userServiceMock);
		String view =  registerController.activateAccountSubmit(activationCode);
		assertEquals("public/register/activation_failed", view);
		
	}
	
	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
