package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.ReviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.SearchCategory;
import com.zuehlke.pgadmissions.domain.enums.SortCategory;
import com.zuehlke.pgadmissions.domain.enums.SortOrder;

public class ApplicationsServiceTest {

	private RegisteredUser user;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationsService applicationsService;

	@Before
	public void setUp() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		applicationsService = new ApplicationsService(applicationFormDAOMock);
	}

	@Test
	public void shouldGetListOfVisibleApplicationsFromDAO() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(form));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, null, null, 1);
		EasyMock.verify(applicationFormDAOMock);
		Assert.assertTrue(visibleApplications.contains(form));
		Assert.assertEquals(1, visibleApplications.size());
	}

	@Test
	public void shouldGetAllApplicationsDueAndUpdatedNotificationToAdmin() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).toApplicationForm(), new ApplicationFormBuilder().id(2).toApplicationForm());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueUpdateNotification = applicationsService.getApplicationsDueUpdateNotification();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueUpdateNotification);
	}

	@Test
	public void shouldGetApplicationsOrderedByCreationDateThenSubmissionDateFirst() throws ParseException {

		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		ApplicationForm appOne = new ApplicationFormBuilder().id(1).appDate(format.parse("01 01 2012")).toApplicationForm();
		ApplicationForm appTwo = new ApplicationFormBuilder().id(2).appDate(format.parse("01 01 2012")).submittedDate(format.parse("01 04 2012")).toApplicationForm();
		ApplicationForm appThree = new ApplicationFormBuilder().id(3).appDate(format.parse("01 02 2012")).toApplicationForm();
		ApplicationForm appFour = new ApplicationFormBuilder().id(4).appDate(format.parse("01 02 2012")).submittedDate(format.parse("01 03 2012")).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(appOne, appTwo, appThree, appFour));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApps = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, null, null, 1);
		EasyMock.verify(applicationFormDAOMock);
		assertEquals(appOne, visibleApps.get(0));
		assertEquals(appThree, visibleApps.get(1));
		assertEquals(appFour, visibleApps.get(2));
		assertEquals(appTwo, visibleApps.get(3));
	}

	@Test
	public void shouldGetApplicationById() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.get(234)).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationById(234));
		EasyMock.verify(applicationFormDAOMock);
	}

	@Test
	public void shouldGetApplicationbyApplicationNumber() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.getApplicationByApplicationNumber("ABC")).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationByApplicationNumber("ABC"));
		EasyMock.verify(applicationFormDAOMock);
	}

	@Test
	public void shouldCreateAndSaveNewApplicationFormWithoutBatchDeadline() throws ParseException {
		Program program = new ProgramBuilder().code("KLOP").id(1).toProgram();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).toUser();
		final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationsService = new ApplicationsService(applicationFormDAOMock) {

			@Override
			ApplicationForm newApplicationForm() {
				return newApplicationForm;
			}
		};
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());
		EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23);
		applicationFormDAOMock.save(newApplicationForm);
		EasyMock.replay(applicationFormDAOMock);
		ApplicationForm returnedForm = applicationsService.createAndSaveNewApplicationForm(registeredUser, program, null);
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(program, returnedForm.getProgram());
		assertEquals("KLOP-2012-000024", returnedForm.getApplicationNumber());
		assertNull(returnedForm.getBatchDeadline());
	}
	
	@Test
	public void shouldCreateAndSaveNewApplicationFormWithBatchDeadline() throws ParseException {
		Program program = new ProgramBuilder().code("KLOP").id(1).toProgram();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).toUser();
		final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(1).toApplicationForm();
		applicationsService = new ApplicationsService(applicationFormDAOMock) {
			
			@Override
			ApplicationForm newApplicationForm() {
				return newApplicationForm;
			}
		};
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());
		EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23);
		applicationFormDAOMock.save(newApplicationForm);
		EasyMock.replay(applicationFormDAOMock);
		ApplicationForm returnedForm = applicationsService.createAndSaveNewApplicationForm(registeredUser, program, "12-Dec-2012");
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(program, returnedForm.getProgram());
		assertEquals("KLOP-2012-000024", returnedForm.getApplicationNumber());
		assertNotNull(returnedForm.getBatchDeadline());
	}

	@Test
	public void shouldReturnCommingFromNullIfNoEvents(){		
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).toApplicationForm();
		Assert.assertNull(applicationsService.getStageComingFrom(application));
	}
	

	
	@Test
	public void shouldReturnPriorEventIfCurrentStagetNotRejectedAndNotApproval() throws ParseException{		
		Event validationEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event reviewEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		Event approvalEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).newStatus(ApplicationFormStatus.APPROVAL).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVAL).events(validationEvent, reviewEvent,approvalEvent).toApplicationForm();
		Assert.assertEquals(ApplicationFormStatus.REVIEW, applicationsService.getStageComingFrom(application));
	}
	@Test
	public void shouldReturCyurrentStatusIfNotStageApproval() throws ParseException{		
		Event validationEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event reviewEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REVIEW).events(validationEvent, reviewEvent).toApplicationForm();
		Assert.assertEquals(ApplicationFormStatus.REVIEW, applicationsService.getStageComingFrom(application));
	}
	
	@Test
	public void shouldPreviousEventIfEventPriorToRejectionNotApproval() throws ParseException {
		Event validationEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event reviewEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		Event rejectedEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).events(validationEvent, rejectedEvent, reviewEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(ApplicationFormStatus.REVIEW, stage);
	}
	@Test
	public void shouldReturnEventBeforePreviousEventIfEventPriorToRejectionIsApproval() throws ParseException {
		Event validationEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event interviewEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.INTERVIEW).toEvent();
		Event approvalEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).newStatus(ApplicationFormStatus.APPROVAL).toEvent();
		Event rejectedEvent = new StateChangeEventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/06/06")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).events(validationEvent, rejectedEvent, approvalEvent, interviewEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(ApplicationFormStatus.INTERVIEW, stage);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionIfSearchTermNotSet() {
		try {
			applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_NUMBER, null, null, null, 1);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertTrue(iae.getMessage().startsWith("Search term cannot be null"));
		}
	}

	@Test
	public void shouldGetAllApplicationsContainingBiologyInTheirNumber() throws ParseException {
		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicationNumber("ABCD").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_NUMBER, "BiOlOgY", null, null, 1);

		EasyMock.verify(applicationFormDAOMock);
		assertEquals(2, applications.size());
	}

	@Test
	public void shouldGetApplicationBelongingToProgramWithCodeScienceAndOtherTitle() {
		Program programOne = new ProgramBuilder().code("Program_ZZZZZ_1").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(applicationFormOne));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.PROGRAMME_NAME, "zzZZz", null, null, 1);

		EasyMock.verify(applicationFormDAOMock);
		assertEquals(1, applications.size());

	}

	@Test
	public void shouldGetApplicationBelongingToProgramWithTitleScienceAndOtherCode() {
		Program programOne = new ProgramBuilder().code("empty").title("Program_ZZZZZ_1").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(applicationFormOne));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.PROGRAMME_NAME, "zzZZz", null, null, 1);

		EasyMock.verify(applicationFormDAOMock);
		assertEquals(1, applications.size());

	}

	@Test
	public void shouldNotReturnAppIfTermNotInProgrameCodeOrTitle() {
		Program programOne = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(applicationFormOne));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.PROGRAMME_NAME, "zzZZz", null, null, 1);

		EasyMock.verify(applicationFormDAOMock);
		assertEquals(0, applications.size());

	}

	@Test
	public void shouldGetApplicationBelongingToApplicantMatchingFirstNameFred() {
		Program programOne = new ProgramBuilder().code("Program_Science_1").title("empty").toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("FredzzZZZZZerick").lastName("Doe").email("email@test.com").username("freddy").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(applicationFormOne));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICANT_NAME, "zzZZz", null, null, 1);

		EasyMock.verify(applicationFormDAOMock);
		assertEquals(1, applications.size());

	}

	@Test
	public void shouldGetApplicationBelongingToApplicantMatchingLastName() {
		Program programOne = new ProgramBuilder().code("Program_Science_1").title("empty").toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Frederick").lastName("FredzzZZZZZerick").email("email@test.com").username("freddy").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(applicationFormOne));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICANT_NAME, "zzZZz", null, null, 1);

		EasyMock.verify(applicationFormDAOMock);
		assertEquals(1, applications.size());

	}

	@Test
	public void shouldNotReturnAppIfTermNotInApplicantNameFirstOrLastName() {
		Program programOne = new ProgramBuilder().code("empty").title("empty").toProgram();
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Frederick").lastName("unique").email("email@test.com").username("freddy").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(applicationFormOne));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICANT_NAME, "empty", null, null, 1);

		EasyMock.verify(applicationFormDAOMock);
		assertEquals(0, applications.size());

	}

	@Test
	public void shouldGetAllApplicationsInValidationStage() throws ParseException {
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).applicationNumber("ABC").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.VALIDATION).applicationNumber("ABCD").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormFour, applicationFormOne, applicationFormThree, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_STATUS, "validati", null, null, 1);
		EasyMock.verify(applicationFormDAOMock);
		assertEquals(2, applications.size());
		assertTrue(applications.contains(applicationFormOne));
		assertTrue(applications.contains(applicationFormThree));
	}

	@Test
	public void shouldGetAllApplicationsInApprovalStage() {
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVAL).applicationNumber("ABC").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).applicant(user).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormFour, applicationFormOne, applicationFormThree, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_STATUS, "approval", null, null, 1);
		EasyMock.verify(applicationFormDAOMock);
		assertEquals(1, applications.size());
		assertTrue(applications.contains(applicationFormOne));

	}

	@Test
	public void shouldGetAllApplicationsInApprovedStage() throws ParseException {
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormFour, applicationFormOne, applicationFormThree, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_STATUS, "approveD", null, null, 1);
		EasyMock.verify(applicationFormDAOMock);
		assertTrue(applications.contains(applicationFormOne));
	}

	@Test
	public void shouldNotReturnAppIfNoStatusMatching() throws ParseException {
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormFour, applicationFormOne, applicationFormThree, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_STATUS, "lalala", null, null, 1);

		EasyMock.verify(applicationFormDAOMock);
		assertEquals(0, applications.size());
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionIfSortOrderNotSet() {
		try {
			applicationsService.getAllVisibleAndMatchedApplications(user, null, null, SortCategory.APPLICANT_NAME, null, 1);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertTrue(iae.getMessage().startsWith("Sort order cannot be null"));
		}
	}

	@Test
	public void shouldSearchAndSort() throws ParseException {
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.INTERVIEW).applicationNumber("ABC").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/04")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.VALIDATION).applicationNumber("ABCD").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/05")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/06")).applicant(user).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormFour, applicationFormThree, applicationFormOne, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, SearchCategory.APPLICATION_NUMBER, "AB", SortCategory.APPLICATION_STATUS, SortOrder.DESCENDING, 1);
		EasyMock.verify(applicationFormDAOMock);
		assertEquals(2, applications.size());
		assertEquals(1, applications.get(0).getId().intValue());
		assertEquals(3, applications.get(1).getId().intValue());
	}

	@Test
	public void shouldSortApplicationInNaturalSortOrder() throws ParseException {
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).applicant(user).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormThree, applicationFormOne, applicationFormFour, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, null, null, 1);

		EasyMock.verify(applicationFormDAOMock);

		Assert.assertEquals(1, applications.get(0).getId().intValue());
		Assert.assertEquals(2, applications.get(1).getId().intValue());
		Assert.assertEquals(3, applications.get(2).getId().intValue());
		Assert.assertEquals(4, applications.get(3).getId().intValue());
	}

	@Test
	public void shouldSortApplicationWithApplName() throws ParseException {
		RegisteredUser applicant1 = new RegisteredUserBuilder().id(1).firstName("AAAA").lastName("BBBB").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant2 = new RegisteredUserBuilder().id(2).firstName("AAAA").lastName("CCCC").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant3 = new RegisteredUserBuilder().id(3).firstName("BBBB").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant4 = new RegisteredUserBuilder().id(4).firstName("CCCC").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).applicant(applicant1).status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicant(applicant2).applicationNumber("App_Biology").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/03")).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).applicant(applicant3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicant(applicant4).applicationNumber("BIOLOGY1").program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2013/03/03")).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormThree, applicationFormOne, applicationFormFour, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, SortCategory.APPLICANT_NAME, SortOrder.ASCENDING, 1);

		EasyMock.verify(applicationFormDAOMock);

		Assert.assertEquals(1, applications.get(0).getId().intValue());
		Assert.assertEquals(2, applications.get(1).getId().intValue());
		Assert.assertEquals(3, applications.get(2).getId().intValue());
		Assert.assertEquals(4, applications.get(3).getId().intValue());
	}

	@Test
	public void shouldSortApplicationWithApplDate() throws ParseException {
		RegisteredUser applicant1 = new RegisteredUserBuilder().id(1).firstName("AAAA").lastName("BBBB").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant2 = new RegisteredUserBuilder().id(2).firstName("AAAA").lastName("CCCC").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant3 = new RegisteredUserBuilder().id(3).firstName("BBBB").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant4 = new RegisteredUserBuilder().id(4).firstName("CCCC").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant1).status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).applicant(applicant2).applicationNumber("App_Biology").program(program).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).applicant(applicant3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/06/06")).applicant(applicant4).applicationNumber("BIOLOGY1").program(program).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormOne, applicationFormThree, applicationFormFour, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, SortCategory.APPLICATION_DATE, SortOrder.ASCENDING, 1);

		EasyMock.verify(applicationFormDAOMock);

		Assert.assertEquals(1, applications.get(0).getId().intValue());
		Assert.assertEquals(4, applications.get(1).getId().intValue());
		Assert.assertEquals(2, applications.get(2).getId().intValue());
		Assert.assertEquals(3, applications.get(3).getId().intValue());
	}

	@Test
	public void shouldSortApplicationWithApplDateDescending() throws ParseException {
		RegisteredUser applicant1 = new RegisteredUserBuilder().id(1).firstName("AAAA").lastName("BBBB").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant2 = new RegisteredUserBuilder().id(2).firstName("AAAA").lastName("CCCC").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant3 = new RegisteredUserBuilder().id(3).firstName("BBBB").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant4 = new RegisteredUserBuilder().id(4).firstName("CCCC").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant1).status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).applicant(applicant2).applicationNumber("App_Biology").program(program).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).submittedDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).applicant(applicant3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/06/06")).applicant(applicant4).applicationNumber("BIOLOGY1").program(program).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormOne, applicationFormThree, applicationFormFour, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, SortCategory.APPLICATION_DATE, SortOrder.DESCENDING, 1);

		EasyMock.verify(applicationFormDAOMock);

		Assert.assertEquals(3, applications.get(0).getId().intValue());
		Assert.assertEquals(2, applications.get(1).getId().intValue());
		Assert.assertEquals(4, applications.get(2).getId().intValue());
		Assert.assertEquals(1, applications.get(3).getId().intValue());
	}

	@Test
	public void shouldSortApplicationWithApplStatus() throws ParseException {
		RegisteredUser applicant1 = new RegisteredUserBuilder().id(1).firstName("AAAA").lastName("BBBB").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant2 = new RegisteredUserBuilder().id(2).firstName("AAAA").lastName("CCCC").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant3 = new RegisteredUserBuilder().id(3).firstName("BBBB").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant4 = new RegisteredUserBuilder().id(4).firstName("CCCC").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant1).applicationNumber("ABC").program(program).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).status(ApplicationFormStatus.INTERVIEW).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/04")).applicant(applicant2).applicationNumber("App_Biology").program(program).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.REVIEW).applicant(applicant3).applicationNumber("ABCD").program(program).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).status(ApplicationFormStatus.UNSUBMITTED).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/05")).applicant(applicant4).applicationNumber("BIOLOGY1").program(program).toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormThree, applicationFormFour, applicationFormOne, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, SortCategory.APPLICATION_STATUS, SortOrder.DESCENDING, 1);

		EasyMock.verify(applicationFormDAOMock);

		Assert.assertEquals(1, applications.get(0).getId().intValue());
		Assert.assertEquals(2, applications.get(1).getId().intValue());
		Assert.assertEquals(3, applications.get(2).getId().intValue());
		Assert.assertEquals(4, applications.get(3).getId().intValue());
	}

	@Test
	public void shouldSortApplicationWithProgramName() throws ParseException {
		RegisteredUser applicant1 = new RegisteredUserBuilder().id(1).firstName("AAAA").lastName("BBBB").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant2 = new RegisteredUserBuilder().id(2).firstName("AAAA").lastName("CCCC").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant3 = new RegisteredUserBuilder().id(3).firstName("BBBB").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		RegisteredUser applicant4 = new RegisteredUserBuilder().id(4).firstName("CCCC").lastName("AAAA").role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program1 = new ProgramBuilder().code("empty").title("AAA").toProgram();
		Program program2 = new ProgramBuilder().code("empty").title("CCC").toProgram();
		Program program3 = new ProgramBuilder().code("empty").title("BBB").toProgram();
		Program program4 = new ProgramBuilder().code("empty").title("DDD").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).program(program1).status(ApplicationFormStatus.APPROVED).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).applicant(applicant1).applicationNumber("ABC").toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).program(program2).status(ApplicationFormStatus.INTERVIEW).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/04")).applicant(applicant2).applicationNumber("App_Biology").toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).program(program3).status(ApplicationFormStatus.REVIEW).applicant(applicant3).applicationNumber("ABCD").toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).program(program4).status(ApplicationFormStatus.UNSUBMITTED).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/05")).applicant(applicant4).applicationNumber("BIOLOGY1").toApplicationForm();

		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(//
				Arrays.asList(applicationFormThree, applicationFormFour, applicationFormOne, applicationFormTwo));
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, SortCategory.PROGRAMME_NAME, SortOrder.DESCENDING, 1);

		EasyMock.verify(applicationFormDAOMock);

		Assert.assertEquals(4, applications.get(0).getId().intValue());
		Assert.assertEquals(2, applications.get(1).getId().intValue());
		Assert.assertEquals(3, applications.get(2).getId().intValue());
		Assert.assertEquals(1, applications.get(3).getId().intValue());
	}

	@Test
	public void shouldLimitApplicationList() throws ParseException {
		List<ApplicationForm> returnedAppls = new ArrayList<ApplicationForm>();
		for (int i = 0; i < 30; i++) {
			ApplicationForm form = new ApplicationFormBuilder().id(i).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/" + (i + 1))).toApplicationForm();
			returnedAppls.add(form);
		}

		Collections.shuffle(returnedAppls);
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(returnedAppls);
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, null, null, 1);

		EasyMock.verify(applicationFormDAOMock);

		Assert.assertEquals(25, applications.size());
	}

	@Test
	public void shouldLimitApplicationListToFifty() throws ParseException {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("AAAA").lastName("BBBB")//
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();

		List<ApplicationForm> returnedAppls = new ArrayList<ApplicationForm>();
		for (int i = 0; i < 45; i++) {
			ApplicationForm form = new ApplicationFormBuilder().id(i).applicant(applicant).status(ApplicationFormStatus.APPROVED)//
					.applicationNumber("ABC" + i).program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).toApplicationForm();
			returnedAppls.add(form);
		}

		Collections.shuffle(returnedAppls);
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(returnedAppls);
		EasyMock.replay(applicationFormDAOMock);

		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications(user, null, null, null, null, 2);

		EasyMock.verify(applicationFormDAOMock);

		Assert.assertEquals(45, applications.size());
	}
	@Test
	public void shouldThrowExceptionIfNegativeBlockCount() throws ParseException {
		RegisteredUser applicant = new RegisteredUserBuilder().id(1).firstName("AAAA").lastName("BBBB")//
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).toRole()).toUser();
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram();

		List<ApplicationForm> returnedAppls = new ArrayList<ApplicationForm>();
		for (int i = 0; i < 45; i++) {
			ApplicationForm form = new ApplicationFormBuilder().id(i).applicant(applicant).status(ApplicationFormStatus.APPROVED)//
					.applicationNumber("ABC" + i).program(program).appDate(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).toApplicationForm();
			returnedAppls.add(form);
		}

		Collections.shuffle(returnedAppls);
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(returnedAppls);
		EasyMock.replay(applicationFormDAOMock);

		try {
			applicationsService.getAllVisibleAndMatchedApplications(user, null, null, null, null, 0);
			Assert.fail("expected exception not thrown!");
		} catch (IllegalArgumentException iae) {
			Assert.assertEquals("Number of application blocks must be greater than 0!", iae.getMessage());
		}
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
