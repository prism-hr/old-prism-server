package com.zuehlke.pgadmissions.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ProgrammeDetail;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.pagemodels.ApplicationPageModel;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SupervisorPropertyEditor;
import com.zuehlke.pgadmissions.services.ProgrammeService;
import com.zuehlke.pgadmissions.services.SupervisorService;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;

public class ProgrammeDetailsControllerTest {

	private RegisteredUser currentUser;
	private ProgrammeDetailsController controller;
	private ProgrammeService programmeDetailsServiceMock;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private DatePropertyEditor datePropertyEditorMock;
	private ProgrammeDetailsValidator programmeDetailsValidatorMock;
	private SupervisorJSONPropertyEditor supervisorJSONPropertyEditorMock;
	private SupervisorService supervisorServiceMock;

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(Supervisor.class, supervisorJSONPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);

	}

	@Test
	public void shouldGetProgrammeDetailsFromService() {
		ProgrammeDetail programmeDetail = new ProgrammeDetailsBuilder().id(1).toProgrammeDetails();
		EasyMock.expect(programmeDetailsServiceMock.getProgrammeDetailsById(1)).andReturn(programmeDetail);
		EasyMock.replay(programmeDetailsServiceMock);

		ProgrammeDetail details = controller.getProgrammeDetails(1);
		assertEquals(programmeDetail, details);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfProgrammeDetailsDoNotExist() {
		EasyMock.expect(programmeDetailsServiceMock.getProgrammeDetailsById(1)).andReturn(null);
		EasyMock.replay(programmeDetailsServiceMock);

		controller.getProgrammeDetails(1);

	}

	@Test
	public void shouldGetNewProgrammeDetailsFromServiceIfIdIsNull() {
		final ProgrammeDetail programmeDetails = new ProgrammeDetailsBuilder().id(1).toProgrammeDetails();

		controller = new ProgrammeDetailsController(programmeDetailsServiceMock, applicationFormPropertyEditorMock,
				datePropertyEditorMock, programmeDetailsValidatorMock, supervisorJSONPropertyEditorMock, supervisorServiceMock) {
			@Override
			ProgrammeDetail newProgrammeDetail() {
				return programmeDetails;
			}
		};
		ProgrammeDetail details = controller.getProgrammeDetails(null);
		assertEquals(programmeDetails, details);
	}

	@Test
	public void shouldSavePersonalDetailsIfNewAndValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

		ProgrammeDetail programmeDetail = new ProgrammeDetail();
		programmeDetailsServiceMock.save(EasyMock.same(programmeDetail));
		EasyMock.replay(errorsMock, programmeDetailsServiceMock);

		controller.editProgrammeDetails(programmeDetail, errorsMock);
		EasyMock.verify(programmeDetailsServiceMock);

	}

	@Test
	public void shouldNotSavePersonalDetailsIfNewAndNotValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);

		ProgrammeDetail programmeDetail = new ProgrammeDetail();

		EasyMock.replay(errorsMock, programmeDetailsServiceMock);

		controller.editProgrammeDetails(programmeDetail, errorsMock);
		EasyMock.verify(programmeDetailsServiceMock);

	}

	@Test
	public void shouldSaveDBIfNotNewAndValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);

		ProgrammeDetail programmeDetail = new ProgrammeDetailsBuilder().id(1).toProgrammeDetails();
		programmeDetailsServiceMock.save(programmeDetail);

		EasyMock.replay(errorsMock, programmeDetailsServiceMock);

		controller.editProgrammeDetails(programmeDetail, errorsMock);
		EasyMock.verify(programmeDetailsServiceMock);

	}

	@Test
	public void shoulNotdFlushToDBIfNotNewButNotdValid() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(true);

		ProgrammeDetail programmeDetail = new ProgrammeDetailsBuilder().id(1).toProgrammeDetails();

		EasyMock.replay(errorsMock, programmeDetailsServiceMock);

		controller.editProgrammeDetails(programmeDetail, errorsMock);
		EasyMock.verify(programmeDetailsServiceMock);

	}

	@Test
	public void shouldSetProgrammeDetailsOnApplicationForm() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).toApplicationForm();
		ProgrammeDetail programmeDetail = new ProgrammeDetailsBuilder().id(1).applicationForm(applicationForm).toProgrammeDetails();

		EasyMock.replay(errorsMock);

		controller.editProgrammeDetails(programmeDetail, errorsMock);
		assertEquals(programmeDetail, applicationForm.getProgrammeDetails());

	}

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowCannotUpdateApplicationExceptionIfApplicationFormNotInUnsubmmitedState() {
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		ProgrammeDetail programmeDetail = new ProgrammeDetailsBuilder().id(1).applicationForm(form).toProgrammeDetails();
		controller.editProgrammeDetails(programmeDetail, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionIfCurrentUserNotApplicant() {
		RegisteredUser applicant = new RegisteredUserBuilder().id(6).toUser();
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(applicant).toApplicationForm();
		ProgrammeDetail programmeDetail = new ProgrammeDetailsBuilder().id(1).applicationForm(form).toProgrammeDetails();
		controller.editProgrammeDetails(programmeDetail, null);

	}

	@Test
	public void shouldReturnApplicationPageModelWithCorrectValues() {
		BindingResult errorsMock = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errorsMock.hasErrors()).andReturn(false);
		ApplicationForm form = new ApplicationFormBuilder().id(2).submissionStatus(SubmissionStatus.UNSUBMITTED).applicant(currentUser).toApplicationForm();
		ProgrammeDetail programmeDetail = new ProgrammeDetailsBuilder().id(5).applicationForm(form).toProgrammeDetails();
		programmeDetailsServiceMock.save(programmeDetail);
		
		EasyMock.replay(errorsMock, programmeDetailsServiceMock);

		
		ModelAndView modelAndView = controller.editProgrammeDetails(programmeDetail, errorsMock);
		assertEquals("private/pgStudents/form/components/programme_details", modelAndView.getViewName());
		ApplicationPageModel model = (ApplicationPageModel) modelAndView.getModel().get("model");
		assertNotNull(model);
		assertEquals(form, model.getApplicationForm());
		assertEquals(currentUser, model.getUser());
		assertEquals(errorsMock, model.getResult());
	}

	@Before
	public void setup() {
		supervisorServiceMock = EasyMock.createMock(SupervisorService.class);

		programmeDetailsServiceMock = EasyMock.createMock(ProgrammeService.class);
		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);
		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		programmeDetailsValidatorMock = EasyMock.createMock(ProgrammeDetailsValidator.class);
		supervisorJSONPropertyEditorMock = EasyMock.createMock(SupervisorJSONPropertyEditor.class);
		controller = new ProgrammeDetailsController(programmeDetailsServiceMock, applicationFormPropertyEditorMock,
				datePropertyEditorMock, programmeDetailsValidatorMock, supervisorJSONPropertyEditorMock,supervisorServiceMock);

		currentUser = new RegisteredUserBuilder().id(1).toUser();
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

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
