package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
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
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.utils.ApplicationPageModelBuilder;

public class ActivateRefereeControllerTest {

	private RefereeService refereeServiceMock;
	private ActivateRefereeController controller;
	private RegisteredUser currentUser;
	private ApplicationPageModelBuilder applicationPageModelBuilderMock;

	@Test
	public void shouldReturnReferencesPageIfLinkIsCorrect(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(form).activationCode("1234").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode(referee.getActivationCode())).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage("1234");
		EasyMock.verify(refereeServiceMock);
		assertNull(((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals(form, ((ApplicationPageModel) modelAndView.getModel().get("model")).getApplicationForm());
		assertEquals(referee, ((ApplicationPageModel) modelAndView.getModel().get("model")).getReferee());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldNotReturnReferencesPageIfActivationCodeIsWrong(){				
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("467")).andReturn(null);
		EasyMock.replay(refereeServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage("467");
		EasyMock.verify(refereeServiceMock);
		assertEquals("Sorry, the system was unable to find you in the system.", ((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Test
	public void shouldReturnExpiredViewForUploadPageIfApplicationFormNotActive(){				
		ApplicationForm form = new ApplicationFormBuilder().id(1).approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(form).activationCode("1234").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode(referee.getActivationCode())).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage("1234");	
		assertEquals("private/referees/upload_references_expired", modelAndView.getViewName());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldNotReturnReferencesPageIfApplicationIdIsWrong(){
		
		Referee referee = new RefereeBuilder().id(1).application(null).activationCode("1234").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode(referee.getActivationCode())).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage("1234");
		EasyMock.verify(refereeServiceMock);
		assertEquals("Sorry, the system was unable to find you in the system.", ((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Test
	public void shouldGetRefereeFromActicationCodeAndReturnApplitactionView() {
		String activationCode = "abc";
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(applicationForm).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode(activationCode)).andReturn(referee);
	

		EasyMock.replay(refereeServiceMock);
		
		ModelAndView modelAndView = controller.getViewApplicationPageForReferee(activationCode);
		assertEquals("private/referees/application/main_application_page", modelAndView.getViewName());
		assertEquals(applicationForm, modelAndView.getModel().get("applicationForm"));
	}
	
	@Test
	public void shouldReturnExpiredViewIfApplicationNotActiveForApplitactionView() {
		String activationCode = "abc";
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		Referee referee = new RefereeBuilder().id(1).application(applicationForm).toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode(activationCode)).andReturn(referee);
		
		EasyMock.replay(refereeServiceMock);
		
		assertEquals("private/referees/upload_references_expired", controller.getViewApplicationPageForReferee(activationCode).getViewName());
	;
	}
	
	@Test
	public void shouldNotGetRegisterPageIfAlreadyRegistered(){
		Role refereeRole = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser refereeUserEnabled = new RegisteredUserBuilder().id(1).role(refereeRole).enabled(true).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		Referee referee = new RefereeBuilder().application(applicationForm).activationCode("123").id(1).firstname("bob").lastname("bobson").user(refereeUserEnabled).email("email@test.com").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("123")).andReturn(referee);
		
		EasyMock.replay(refereeServiceMock, applicationPageModelBuilderMock);
		ModelAndView registerPage = controller.getRefereeRegisterPage(referee.getActivationCode());
		assertEquals("private/referees/already_registered", registerPage.getViewName());
		
	}
	@Test
	public void shouldNotGetRegisterPageIfNotRegistered(){
		Role refereeRole = new RoleBuilder().authorityEnum(Authority.REFEREE).toRole();
		RegisteredUser refereeUserDisabled = new RegisteredUserBuilder().id(1).role(refereeRole).enabled(false).toUser();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicant(new RegisteredUserBuilder().toUser()).toApplicationForm();
		Referee referee = new RefereeBuilder().application(applicationForm).activationCode("123").id(1).firstname("bob").lastname("bobson").user(refereeUserDisabled).email("email@test.com").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("123")).andReturn(referee);
		
		EasyMock.replay(refereeServiceMock, applicationPageModelBuilderMock);
		ModelAndView registerPage = controller.getRefereeRegisterPage(referee.getActivationCode());
		assertEquals("private/referees/register_referee", registerPage.getViewName());
		
	}
	
	@Before
	public void setup() {
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		applicationPageModelBuilderMock = EasyMock.createMock(ApplicationPageModelBuilder.class);
		controller = new ActivateRefereeController(refereeServiceMock, applicationPageModelBuilderMock);

		currentUser = new RegisteredUserBuilder().id(1).toUser();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}
	
}
