package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ApplicationsServiceTest {

	private RegisteredUser user;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationsService applicationsService;

	@Before
	public void setup() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null);
		user = new RegisteredUserBuilder().id(1).username("bob")
				.role(new RoleBuilder().authorityEnum(Authority.APPLICANT).build()).build();
		authenticationToken.setDetails(user);
		SecurityContextImpl secContext = new SecurityContextImpl();
		secContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(secContext);

		applicationFormDAOMock = EasyMock.createMock(ApplicationFormDAO.class);
		applicationsService = new ApplicationsService(applicationFormDAOMock, null);
	}

	@Test
	public void shouldGetAllApplicationsDueAndUpdatedNotificationToAdmin() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).build(),
				new ApplicationFormBuilder().id(2).build());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueUpdateNotification = applicationsService.getApplicationsDueUpdateNotification();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueUpdateNotification);
	}

	@Test
	public void shouldGetAllApplicationsDueRegistryNotification() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).build(),
				new ApplicationFormBuilder().id(2).build());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueRegistryNotification()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueRegistryNotification = applicationsService
				.getApplicationsDueRegistryNotification();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueRegistryNotification);
	}

	@Test
	public void shouldGetAllApplicationsDueApprovalRestartRequestNotification() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).build(),
				new ApplicationFormBuilder().id(2).build());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueApprovalRequestNotification()).andReturn(
				applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueNotification = applicationsService
				.getApplicationsDueApprovalRestartRequestNotification();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueNotification);
	}

	@Test
	public void shouldGetAllApplicationsDueApprovalRestartRequestReminder() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).build(),
				new ApplicationFormBuilder().id(2).build());
		EasyMock.expect(applicationFormDAOMock.getApplicationDueApprovalRestartRequestReminder()).andReturn(
				applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueNotification = applicationsService
				.getApplicationsDueApprovalRestartRequestReminder();
		EasyMock.verify(applicationFormDAOMock);
		assertSame(applicationsList, appsDueNotification);
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
	public void shouldCreateAndSaveNewApplicationFormWithoutBatchDeadlineProjectOrResearchHomePage()
			throws ParseException {
		Program program = new ProgramBuilder().code("KLOP").id(1).build();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());

		// one existing application, since is approved should be ignored
		final ApplicationForm existingApplicationForm = new ApplicationFormBuilder().id(1)
				.status(ApplicationFormStatus.APPROVED).build();
		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program))
				.andReturn(Lists.newArrayList(existingApplicationForm));

		EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23L);
		applicationFormDAOMock.save(EasyMock.isA(ApplicationForm.class));
		EasyMock.replay(applicationFormDAOMock);
		ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser,
				program, null, null, null);
		EasyMock.verify(applicationFormDAOMock);
		assertNotNull(returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(program, returnedForm.getProgram());
		assertEquals("KLOP-" + thisYear + "-000024", returnedForm.getApplicationNumber());
		assertNull(returnedForm.getBatchDeadline());
	}

	@Test
	public void shouldCreateAndSaveNewApplicationFormWithBatchDeadlineProjectAndResearchHomePage()
			throws ParseException {
		Program program = new ProgramBuilder().code("KLOP").id(1).build();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
		String thisYear = new SimpleDateFormat("yyyy").format(new Date());

		// one existing application, since is withdrawn should be ignored
		final ApplicationForm existingApplicationForm = new ApplicationFormBuilder().id(1)
				.status(ApplicationFormStatus.WITHDRAWN).build();
		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program))
				.andReturn(Lists.newArrayList(existingApplicationForm));

		EasyMock.expect(applicationFormDAOMock.getApplicationsInProgramThisYear(program, thisYear)).andReturn(23L);
		applicationFormDAOMock.save(EasyMock.isA(ApplicationForm.class));
		EasyMock.replay(applicationFormDAOMock);
		Date batchDeadline = new SimpleDateFormat("dd/MM/yyyy").parse("12/12/2012");
		String projectTitle = "This is the project title";
		String researchHomePage = "researchHomePage";
		ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser,
				program, batchDeadline, projectTitle, researchHomePage);
		EasyMock.verify(applicationFormDAOMock);
		assertNotNull(returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(program, returnedForm.getProgram());
		assertEquals("KLOP-" + thisYear + "-000024", returnedForm.getApplicationNumber());
		assertEquals(batchDeadline, returnedForm.getBatchDeadline());
		assertEquals(projectTitle, returnedForm.getProjectTitle());
		assertEquals("http://" + researchHomePage, returnedForm.getResearchHomePage());
	}

	@Test
	public void shouldGetRecentlyEditedUnsubmittedApplicationForGivenQueryString() throws ParseException {

		// GIVEN
		Date date = new Date();

		Program program = new ProgramBuilder().code("KLOP").id(1).build();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
		final ApplicationForm oldApplicationForm = new ApplicationFormBuilder().id(1)
				.status(ApplicationFormStatus.UNSUBMITTED).lastUpdated(date).build();
		final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(2)
				.status(ApplicationFormStatus.UNSUBMITTED).lastUpdated(DateUtils.addDays(date, 2)).build();
		final ApplicationForm oldApplicationForm2 = new ApplicationFormBuilder().id(3)
				.status(ApplicationFormStatus.UNSUBMITTED).lastUpdated(DateUtils.addDays(date, 1)).build();

		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program))
				.andReturn(Lists.newArrayList(oldApplicationForm, newApplicationForm, oldApplicationForm2));

		// WHEN
		EasyMock.replay(applicationFormDAOMock);
		ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser,
				program, null, null, null);

		// THEN
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
	}

	@Test
	public void shouldGetInterviewedApplicationForGivenQueryString() throws ParseException {

		// GIVEN
		Date date = new Date();

		Program program = new ProgramBuilder().code("KLOP").id(1).build();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
		final ApplicationForm validationApplicationForm = new ApplicationFormBuilder().id(1)
				.status(ApplicationFormStatus.VALIDATION).lastUpdated(date).build();
		final ApplicationForm unsubmittedApplicationForm = new ApplicationFormBuilder().id(2)
				.status(ApplicationFormStatus.UNSUBMITTED).lastUpdated(DateUtils.addDays(date, 2)).build();
		final ApplicationForm interviewApplicationForm = new ApplicationFormBuilder().id(3)
				.status(ApplicationFormStatus.INTERVIEW).lastUpdated(DateUtils.addDays(date, 1)).build();

		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program))
				.andReturn(
						Lists.newArrayList(validationApplicationForm, unsubmittedApplicationForm,
								interviewApplicationForm));

		// WHEN
		EasyMock.replay(applicationFormDAOMock);
		ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser,
				program, null, null, null);

		// THEN
		EasyMock.verify(applicationFormDAOMock);
		assertSame(interviewApplicationForm, returnedForm);
	}

	@Test
	public void shouldGetMostRecentApprovalApplicationForGivenQueryString() throws ParseException {

		// GIVEN
		Date date = new Date();

		Program program = new ProgramBuilder().code("KLOP").id(1).build();
		RegisteredUser registeredUser = new RegisteredUserBuilder().id(1).build();
		final ApplicationForm oldApplicationForm = new ApplicationFormBuilder().id(1)
				.status(ApplicationFormStatus.APPROVAL).lastUpdated(date).build();
		final ApplicationForm newApplicationForm = new ApplicationFormBuilder().id(2)
				.status(ApplicationFormStatus.APPROVAL).lastUpdated(DateUtils.addDays(date, 2)).build();
		final ApplicationForm oldApplicationForm2 = new ApplicationFormBuilder().id(3)
				.status(ApplicationFormStatus.APPROVAL).lastUpdated(DateUtils.addDays(date, 1)).build();

		EasyMock.expect(applicationFormDAOMock.getApplicationsByApplicantAndProgram(registeredUser, program))
				.andReturn(Lists.newArrayList(oldApplicationForm, newApplicationForm, oldApplicationForm2));

		// WHEN
		EasyMock.replay(applicationFormDAOMock);
		ApplicationForm returnedForm = applicationsService.createOrGetUnsubmittedApplicationForm(registeredUser,
				program, null, null, null);

		// THEN
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
