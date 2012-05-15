package com.zuehlke.pgadmissions.controllers.referees;

import static org.junit.Assert.assertEquals;

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
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.RefereeService;

import cucumber.annotation.After;

public class ActivateRefereeControllerTest {

	private RefereeService refereeServiceMock;
	private ActivateRefereeController controller;
	private RegisteredUser currentUser;
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

		EasyMock.replay(refereeServiceMock);
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

		EasyMock.replay(refereeServiceMock);
		ModelAndView registerPage = controller.getRefereeRegisterPage(referee.getActivationCode(), modelMap);
		assertEquals("private/referees/register_referee", registerPage.getViewName());

	}

	@Before
	public void setup() {
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		controller = new ActivateRefereeController(refereeServiceMock);

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
