package com.zuehlke.pgadmissions.controllers.workflow.approval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DirectURLsEnum;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.NewUserByAdminValidator;

public class CreateNewSupervisorControllerTest {
	
	private CreateNewSupervisorController controller;
	private UserService userServiceMock;	
	private BindingResult bindingResultMock;	
	private ApplicationsService applicationsServiceMock;
	private NewUserByAdminValidator newUserValidatorMock;
	private RegisteredUser currentUserMock;

	@Test
	public void shouldCreateNewUserIfUserDoesNotExists() {
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").build();
		RegisteredUser suggestedUser = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").build();
		RegisteredUser user = new RegisteredUserBuilder().id(6).firstName("bob").lastName("bobson").email("bobson@bob.com").build();
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(null);
		EasyMock.expect(userServiceMock.createNewUserInRole("bob", "bobson", "bobson@bob.com", DirectURLsEnum.VIEW_APPLIATION_AS_SUPERVISOR, application, Authority.SUPERVISOR)).andReturn(user);
		EasyMock.replay(userServiceMock);

		ModelAndView modelAndView = controller.createNewSupervisorUser(suggestedUser, bindingResultMock, application);
		Assert.assertEquals("/private/staff/reviewer/reviewer_json", modelAndView.getViewName());
		assertEquals(user, modelAndView.getModel().get("user"));
		assertTrue((Boolean)modelAndView.getModel().get("isNew"));
	}

	@Test	
	public void shouldReturnExistingUserIfUserAlreadyExists() {
		EasyMock.reset(userServiceMock);
		ApplicationForm application = new ApplicationFormBuilder().id(2).applicationNumber("ABC").build();
		RegisteredUser suggestedUser = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").build();
		RegisteredUser existingUser = new RegisteredUserBuilder().id(6).firstName("bob").lastName("bobson").email("bobson@bob.com").build();
	
		EasyMock.expect(userServiceMock.getUserByEmailIncludingDisabledAccounts("bobson@bob.com")).andReturn(existingUser);
		EasyMock.replay(userServiceMock);

		ModelAndView modelAndView = controller.createNewSupervisorUser(suggestedUser, bindingResultMock, application);
		Assert.assertEquals("/private/staff/reviewer/reviewer_json", modelAndView.getViewName());
		EasyMock.verify(userServiceMock);
		assertEquals(existingUser, modelAndView.getModel().get("user"));
		assertFalse((Boolean)modelAndView.getModel().get("isNew"));
	}
	

	

	@Test
	public void shouldReturnToViewIfValidationErrors() {
		EasyMock.reset(bindingResultMock);
		RegisteredUser user = new RegisteredUserBuilder().id(5).firstName("bob").lastName("bobson").email("bobson@bob.com").build();
		EasyMock.expect(bindingResultMock.hasErrors()).andReturn(true);
		EasyMock.replay(bindingResultMock);
		ModelAndView modelAndView = controller.createNewSupervisorUser(user, bindingResultMock, null);
		Assert.assertEquals("/private/staff/supervisors/create_supervisor_section", modelAndView.getViewName());

	}
	
	@Test
	public void shouldGetCreateSupervisorsSection() {		
		Assert.assertEquals("/private/staff/supervisors/create_supervisor_section", controller.getCreateSupervisorSection());
	}
	@Test
	public void shouldBindValidator() {		
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(newUserValidatorMock);
		binderMock.registerCustomEditor(EasyMock.eq(String.class), EasyMock.anyObject(StringTrimmerEditor.class));
		
		EasyMock.replay(binderMock);
		controller.registerSupervisorValidators(binderMock);
		EasyMock.verify(binderMock);
	}
	
	@Test
	public void shouldGetNewUserAsSupervisor() {		
		RegisteredUser reviewer = controller.getSupervisor();
		assertNull(reviewer.getId());
	}

	@Test
	public void shouldGetApplicationFromIdForAdmin() {
		Program program = new ProgramBuilder().id(6).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).advert(program).build();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(true);
		EasyMock.expect(currentUserMock.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test
	public void shouldGetApplicationFromIdForSupervisor() {
		Program program = new ProgramBuilder().id(6).build();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).advert(program).build();

		EasyMock.expect(currentUserMock.hasAdminRightsOnApplication(applicationForm)).andReturn(false);
		EasyMock.expect(currentUserMock.isSupervisorOfApplicationForm(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUserMock);

		ApplicationForm returnedForm = controller.getApplicationForm("5");
		assertEquals(applicationForm, returnedForm);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfApplicatioDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);

		controller.getApplicationForm("5");
	}

	@Before
	public void setup() {
		newUserValidatorMock = EasyMock.createMock(NewUserByAdminValidator.class);
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);
		userServiceMock = EasyMock.createMock(UserService.class);
		bindingResultMock = EasyMock.createMock(BindingResult.class);
		currentUserMock = EasyMock.createMock(RegisteredUser.class);
		EasyMock.expect(userServiceMock.getCurrentUser()).andReturn(currentUserMock).anyTimes();
		EasyMock.replay(userServiceMock);
		controller = new CreateNewSupervisorController(applicationsServiceMock, userServiceMock,  newUserValidatorMock);
	}
}