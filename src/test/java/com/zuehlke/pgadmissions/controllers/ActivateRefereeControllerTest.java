package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.exceptions.RefereeAlreadyUploadedReference;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.services.RefereeService;

public class ActivateRefereeControllerTest {

	private RefereeService refereeServiceMock;
	private ActivateRefereeController controller;
	private RegisteredUser currentUser;

	@Test
	public void shouldReturnReferencesPageIfLinkIsCorrect(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).application(form).activationCode("1234").toReferee();
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
	public void shouldNotReturnReferencesPageIfAvtivationCodeIsWrong(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).application(form).activationCode("1234").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode("467")).andReturn(null);
		EasyMock.replay(refereeServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage("467");
		EasyMock.verify(refereeServiceMock);
		assertEquals("Sorry, the system was unable to find you in the system.", ((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void shouldNotReturnReferencesPageIfApplicationIdIsWrong(){
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		Referee referee = new RefereeBuilder().refereeId(1).application(null).activationCode("1234").toReferee();
		EasyMock.expect(refereeServiceMock.getRefereeByActivationCode(referee.getActivationCode())).andReturn(referee);
		EasyMock.replay(refereeServiceMock);
		ModelAndView modelAndView = controller.getReferencesPage("1234");
		EasyMock.verify(refereeServiceMock);
		assertEquals("Sorry, the system was unable to find you in the system.", ((ApplicationPageModel) modelAndView.getModel().get("model")).getMessage());
		assertEquals("private/referees/upload_references", modelAndView.getViewName());
	}
	
	@Before
	public void setup() {
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		controller = new ActivateRefereeController(refereeServiceMock);

		currentUser = new RegisteredUserBuilder().id(1).toUser();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		authenticationToken.setDetails(currentUser);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);
	}
	
}
