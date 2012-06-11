package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgrammeDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.Referrer;
import com.zuehlke.pgadmissions.domain.enums.StudyOption;
import com.zuehlke.pgadmissions.exceptions.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.ApplicationFormPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DatePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SuggestedSupervisorJSONPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ProgrammeDetailsService;
import com.zuehlke.pgadmissions.validators.ProgrammeDetailsValidator;

public class ProgrammeDetailsControllerTest {
	private RegisteredUser currentUser;
	private DatePropertyEditor datePropertyEditorMock;
	private ApplicationsService applicationsServiceMock;
	private ProgrammeDetailsValidator programmeDetailsValidatorMock;
	private ProgrammeDetailsService programmeDetailsServiceMock;
	private ProgrammeDetailsController controller;
	private ApplicationFormPropertyEditor applicationFormPropertyEditorMock;
	private UsernamePasswordAuthenticationToken authenticationToken;
	private SuggestedSupervisorJSONPropertyEditor supervisorJSONPropertyEditorMock;

	@Test(expected = CannotUpdateApplicationException.class)
	public void shouldThrowExceptionIfApplicationFormNotModifiableOnPost() {
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(1)
				.applicationForm(new ApplicationFormBuilder().id(5).status(ApplicationFormStatus.APPROVED).toApplicationForm()).toProgrammeDetails();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.replay(programmeDetailsServiceMock, errors);
		controller.editProgrammeDetails(programmeDetails, errors);
		EasyMock.verify(programmeDetailsServiceMock);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnSubmitIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.editProgrammeDetails(null, null);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourenotFoundExceptionOnGetIfCurrentUserNotApplicant() {
		currentUser.getRoles().clear();
		controller.getProgrammeDetailsView();
	}

	@Test
	public void shouldReturnProgrammeDetailsView() {
		assertEquals("/private/pgStudents/form/components/programme_details", controller.getProgrammeDetailsView());
	}

	@Test
	public void shouldReturnAvaialbeStudyOptionLevels() {
		final String applicationNumber = "1";
		Program program = new ProgramBuilder().id(7).toProgram();
		final ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).applicationNumber(applicationNumber).program(program).toApplicationForm();
		controller =  new ProgrammeDetailsController(applicationsServiceMock, applicationFormPropertyEditorMock, datePropertyEditorMock,
				supervisorJSONPropertyEditorMock, programmeDetailsValidatorMock, programmeDetailsServiceMock){

					@Override
					public ApplicationForm getApplicationForm(String id) {
						if(applicationNumber == id){
							return applicationForm;
						}
						return null;
					}
			
		};

		EasyMock.expect(programmeDetailsServiceMock.getAvailableStudyOptions(program)).andReturn(
				Arrays.asList(StudyOption.FULL_TIME, StudyOption.PART_TIME_DISTANCE));
		EasyMock.replay(programmeDetailsServiceMock);
		StudyOption[] studyOptions = controller.getStudyOptions(applicationNumber);
		assertArrayEquals(studyOptions, new StudyOption[] { StudyOption.FULL_TIME, StudyOption.PART_TIME_DISTANCE });
	}

	@Test
	public void shouldReturnAllReferers() {
		Referrer[] referrers = controller.getReferrers();
		assertArrayEquals(referrers, Referrer.values());
	}

	@Test
	public void shouldReturnApplicationForm() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.replay(applicationsServiceMock, currentUser);
		ApplicationForm returnedApplicationForm = controller.getApplicationForm("1");
		assertEquals(applicationForm, returnedApplicationForm);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNoFoundExceptionIfApplicationFormDoesNotExist() {
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(null);
		EasyMock.replay(applicationsServiceMock);
		controller.getApplicationForm("1");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void shouldThrowResourceNotFoundExceptionIfUserCAnnotSeeApplFormOnGet() {
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("1")).andReturn(applicationForm);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(false);
		EasyMock.replay(applicationsServiceMock, currentUser);
		controller.getApplicationForm("1");

	}

	@Test
	public void shouldBindPropertyEditors() {
		WebDataBinder binderMock = EasyMock.createMock(WebDataBinder.class);
		binderMock.setValidator(programmeDetailsValidatorMock);
		binderMock.registerCustomEditor(Date.class, datePropertyEditorMock);
		binderMock.registerCustomEditor(ApplicationForm.class, applicationFormPropertyEditorMock);
		binderMock.registerCustomEditor(SuggestedSupervisor.class, supervisorJSONPropertyEditorMock);
		EasyMock.replay(binderMock);
		controller.registerPropertyEditors(binderMock);
		EasyMock.verify(binderMock);
	}

	@Test
	public void shouldGetProgrammeDetailsFromApplicationForm() {

		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(1).toProgrammeDetails();
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		applicationForm.setProgrammeDetails(programmeDetails);
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(applicationsServiceMock, currentUser);

		ProgrammeDetails returnedProgrammeDetails = controller.getProgrammeDetails("5");
		assertEquals(programmeDetails, returnedProgrammeDetails);
	}

	@Test
	public void shouldReturnNewProgrammeDetailsIfApplicationFormHasNoProgrammeDetails() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).toApplicationForm();
		EasyMock.expect(applicationsServiceMock.getApplicationByApplicationNumber("5")).andReturn(applicationForm);
		currentUser = EasyMock.createMock(RegisteredUser.class);
		authenticationToken.setDetails(currentUser);
		EasyMock.expect(currentUser.canSee(applicationForm)).andReturn(true);
		EasyMock.replay(applicationsServiceMock, currentUser);
		ProgrammeDetails returnedProgrammeDetails = controller.getProgrammeDetails("5");
		assertNull(returnedProgrammeDetails.getId());
	}

	

	@Test
	public void shouldReturnMessage() {
		assertEquals("bob", controller.getMessage("bob"));

	}

	@Test
	public void shouldSaveProgrammeDetailsAndApplicationAndRedirectIfNoErrors() {
		ApplicationForm applicationForm = new ApplicationFormBuilder().id(5).applicationNumber("ABC").toApplicationForm();
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(1).applicationForm(applicationForm).toProgrammeDetails();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(false);
		programmeDetailsServiceMock.save(programmeDetails);
		applicationsServiceMock.save(applicationForm);
		EasyMock.replay(programmeDetailsServiceMock,applicationsServiceMock, errors);
		String view = controller.editProgrammeDetails(programmeDetails, errors);
		EasyMock.verify(programmeDetailsServiceMock, applicationsServiceMock);
		assertEquals("redirect:/update/getProgrammeDetails?applicationId=ABC", view);
		assertEquals(DateUtils.truncate(Calendar.getInstance().getTime(),Calendar.DATE), DateUtils.truncate(applicationForm.getLastUpdated(), Calendar.DATE));
	}

	@Test
	public void shouldNotSaveAndReturnToViewIfErrors() {
		ProgrammeDetails programmeDetails = new ProgrammeDetailsBuilder().id(1).applicationForm(new ApplicationFormBuilder().id(5).toApplicationForm())
				.toProgrammeDetails();
		BindingResult errors = EasyMock.createMock(BindingResult.class);
		EasyMock.expect(errors.hasErrors()).andReturn(true);

		EasyMock.replay(programmeDetailsServiceMock, errors);
		String view = controller.editProgrammeDetails(programmeDetails, errors);
		EasyMock.verify(programmeDetailsServiceMock);
		assertEquals("/private/pgStudents/form/components/programme_details", view);
	}

	@Before
	public void setUp() {
		applicationsServiceMock = EasyMock.createMock(ApplicationsService.class);

		programmeDetailsServiceMock = EasyMock.createMock(ProgrammeDetailsService.class);

		applicationFormPropertyEditorMock = EasyMock.createMock(ApplicationFormPropertyEditor.class);

		datePropertyEditorMock = EasyMock.createMock(DatePropertyEditor.class);
		supervisorJSONPropertyEditorMock = EasyMock.createMock(SuggestedSupervisorJSONPropertyEditor.class);
		programmeDetailsValidatorMock = EasyMock.createMock(ProgrammeDetailsValidator.class);
		programmeDetailsServiceMock = EasyMock.createMock(ProgrammeDetailsService.class);

		controller = new ProgrammeDetailsController(applicationsServiceMock, applicationFormPropertyEditorMock, datePropertyEditorMock,
				supervisorJSONPropertyEditorMock, programmeDetailsValidatorMock, programmeDetailsServiceMock);

		authenticationToken = new UsernamePasswordAuthenticationToken(null, null);

		currentUser = new RegisteredUserBuilder().id(1).role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
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
