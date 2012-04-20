package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.utils.ApplicationPageModelBuilder;

import cucumber.annotation.After;

public class ActivateRefereeControllerTest {

	private RefereeService refereeServiceMock;
	private ActivateRefereeController controller;
	private RegisteredUser currentUser;
	private ApplicationPageModelBuilder applicationPageModelBuilderMock;
	private ApplicationsService applicationServiceMock;
	private UsernamePasswordAuthenticationToken authenticationToken;

	@Test
	public void shouldNotGetRegisterPageIfAlreadyRegistered() {
		ModelMap modelMap = new ModelMap();
		Role refereeRole = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser refereeUserEnabled = new RegisteredUserBuilder().id(1).role(refereeRole).enabled(true).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		Referee referee = new RefereeBuilder().application(applicationForm).activationCode("123").id(1).firstname("bob").lastname("bobson")
				.user(refereeUserEnabled).email("email@test.com").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("123")).andReturn(referee);

		EasyMock.replay(refereeServiceMock, applicationPageModelBuilderMock);
		ModelAndView registerPage = controller.getRefereeRegisterPage(referee.getActivationCode(), modelMap);
		assertEquals("private/referees/already_registered", registerPage.getViewName());

	}

	@Test
	public void shouldNotGetRegisterPageIfNotRegistered() {
		ModelMap modelMap = new ModelMap();
		Role refereeRole = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser refereeUserDisabled = new RegisteredUserBuilder().id(1).role(refereeRole).enabled(false).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		Referee referee = new RefereeBuilder().application(applicationForm).activationCode("123").id(1).firstname("bob").lastname("bobson")
				.user(refereeUserDisabled).email("email@test.com").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("123")).andReturn(referee);

		EasyMock.replay(refereeServiceMock, applicationPageModelBuilderMock);
		ModelAndView registerPage = controller.getRefereeRegisterPage(referee.getActivationCode(), modelMap);
		assertEquals("private/referees/register_referee", registerPage.getViewName());

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundexceptionIfApplicationDoesNotExist() {

		EasyMock.expect(applicationServiceMock.getApplicationById(1)).andReturn(null);
		EasyMock.replay(applicationServiceMock);
		controller.getUploadReferencesPage(1, null);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundexceptionIfCurrentUserNotRefereeOfApplicationForm() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock);
		EasyMock.expect(userMock.isRefereeOfApplicationForm(applicationForm)).andReturn(false);
		EasyMock.expect(userMock.isInRole(Authority.SUPERADMINISTRATOR)).andReturn(false);
		EasyMock.replay(userMock);
		controller.getUploadReferencesPage(1, null);

	}

	@Test
	public void shouldNotThrowResourceNotFoundexceptionIfCurrentUserNotRefereeOfApplicationFormButIsSuperADmin() {
		RegisteredUser user = new RegisteredUserBuilder().role(new RoleBuilder().authorityEnum(Authority.SUPERADMINISTRATOR).toRole()).toUser();
		authenticationToken.setDetails(user);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock);

		assertNotNull(controller.getUploadReferencesPage(1, new ModelMap()));

	}

	@Test
	public void shouldGetUploadReferencePageWithCorrectModelAttributes() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		ModelMap modelMap = new ModelMap();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationServiceMock.getApplicationById(1)).andReturn(applicationForm);

		Referee referee = new RefereeBuilder().id(1).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByUserAndApplication(userMock, applicationForm)).andReturn(referee);
		EasyMock.expect(userMock.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.replay(applicationServiceMock, userMock, refereeServiceMock);
		ModelAndView modelAndView = controller.getUploadReferencesPage(1, modelMap);

		assertEquals("private/referees/upload_references", modelAndView.getViewName());
		assertEquals(applicationForm, modelAndView.getModel().get("application"));
		assertEquals(userMock, modelAndView.getModel().get("user"));
		assertEquals(referee, modelAndView.getModel().get("referee"));
	}

	@Test
	public void shouldReturnExpiredViewIfApplicationFormAlreadyDecided() {
		RegisteredUser userMock = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(userMock);
		ModelMap modelMap = new ModelMap();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		EasyMock.expect(applicationServiceMock.getApplicationById(1)).andReturn(applicationForm);
		EasyMock.replay(applicationServiceMock);
		EasyMock.expect(userMock.isRefereeOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.replay(userMock);
		ModelAndView modelAndView = controller.getUploadReferencesPage(1, modelMap);

		assertEquals("private/referees/upload_references_expired", modelAndView.getViewName());
		assertEquals(applicationForm, modelAndView.getModel().get("application"));
		assertEquals(userMock, modelAndView.getModel().get("user"));
	}

	@Before
	public void setup() {
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		applicationPageModelBuilderMock = EasyMock.createMock(ApplicationPageModelBuilder.class);
		applicationServiceMock = EasyMock.createMock(ApplicationsService.class);
		controller = new ActivateRefereeController(refereeServiceMock, applicationPageModelBuilderMock, applicationServiceMock);

		currentUser = new RegisteredUserBuilder().id(1).toUser();
		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

}
