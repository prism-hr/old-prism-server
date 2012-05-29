package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

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
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EventBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RoleBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ApplicationsServiceTest {

	private RegisteredUser user;
	private ApplicationFormDAO applicationFormDAOMock;
	private ApplicationsService applicationsService;

	@Test
	public void shouldGetListOfVisibleApplicationsFromDAO() {
		ApplicationForm form = new ApplicationFormBuilder().id(1).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(form));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApplications = applicationsService.getVisibleApplications(user);
		Assert.assertTrue(visibleApplications.contains(form));
		Assert.assertEquals(1, visibleApplications.size());
	}

	@Test
	public void shouldGetAllApplicationsDueAndUpdatedNotificationToAdmin() {
		List<ApplicationForm> applicationsList = Arrays.asList(new ApplicationFormBuilder().id(1).toApplicationForm(), new ApplicationFormBuilder().id(2)
				.toApplicationForm());
		EasyMock.expect(applicationFormDAOMock.getApplicationsDueUpdateNotification()).andReturn(applicationsList);
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> appsDueUpdateNotification = applicationsService.getApplicationsDueUpdateNotification();
		assertSame(applicationsList, appsDueUpdateNotification);
	}

	
	@Test
	public void shouldGetApplicationsOrderedBysubmissionDateThenCreationDateFirst() throws  ParseException {
		
		SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
		ApplicationForm appOne = new ApplicationFormBuilder().id(1).appDate(format.parse("01 01 2012")).toApplicationForm();
		ApplicationForm appTwo = new ApplicationFormBuilder().id(2).appDate(format.parse("01 01 2012")).submittedDate(format.parse("01 04 2012")).toApplicationForm();
		ApplicationForm appThree = new ApplicationFormBuilder().id(3).appDate(format.parse("01 02 2012")).toApplicationForm();
		ApplicationForm appFour = new ApplicationFormBuilder().id(4).appDate(format.parse("01 02 2012")).submittedDate(format.parse("01 03 2012")).toApplicationForm();
		EasyMock.expect(applicationFormDAOMock.getVisibleApplications(user)).andReturn(Arrays.asList(appOne, appTwo, appThree, appFour));
		EasyMock.replay(applicationFormDAOMock);
		List<ApplicationForm> visibleApps = applicationsService.getVisibleApplications(user);
		assertEquals(appTwo, visibleApps.get(0));
		assertEquals(appFour, visibleApps.get(1));
		assertEquals(appThree, visibleApps.get(2));
		assertEquals(appOne, visibleApps.get(3));
	}
	
	@Test
	public void shouldGetApplicationById() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.get(234)).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationById(234));
	}
	
	@Test
	public void shouldGetApplicationbyApplicationNumber() {
		ApplicationForm application = EasyMock.createMock(ApplicationForm.class);
		EasyMock.expect(applicationFormDAOMock.getApplicationByApplicationNumber("ABC")).andReturn(application);

		EasyMock.replay(application, applicationFormDAOMock);
		Assert.assertEquals(application, applicationsService.getApplicationByApplicationNumber("ABC"));
	}
	
	@Test	
	public void shouldCreateAndSaveNewApplicationForm() {
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
		ApplicationForm returnedForm = applicationsService.createAndSaveNewApplicationForm(registeredUser, program);
		EasyMock.verify(applicationFormDAOMock);
		assertSame(newApplicationForm, returnedForm);
		assertEquals(registeredUser, returnedForm.getApplicant());
		assertEquals(program, returnedForm.getProgram());
		assertEquals("KLOP-2012-24", returnedForm.getApplicationNumber());

	}

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
	public void shouldReturnReviewIfRejectedInReviewPhase() throws ParseException{
		Event validationEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event reviewEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		Event rejectedEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).events(validationEvent, rejectedEvent, reviewEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(ApplicationFormStatus.REVIEW, stage);
	}
	
	@Test
	public void shouldReturnInterviewIfRejectedInInterviewPhase() throws ParseException{
		Event validationEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event interviewEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.INTERVIEW).toEvent();
		Event rejectedEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).events(validationEvent, rejectedEvent, interviewEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(ApplicationFormStatus.INTERVIEW, stage);
	}
	
	@Test
	public void shouldReturnInterviewIfRejectedInApprovalPhaseAndPreviousOfApprovalIsInterview() throws ParseException{
		Event validationEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event interviewEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.INTERVIEW).toEvent();
		Event approvalEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.APPROVAL).toEvent();
		Event rejectedEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).events(validationEvent, rejectedEvent, interviewEvent, approvalEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(ApplicationFormStatus.INTERVIEW, stage);

	}
	
	@Test
	public void shouldReturnReviewIfRejectedInApprovalPhaseAndPreviousOfApprovalIsReview() throws ParseException{
		Event validationEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event reviewEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		Event approvalEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.APPROVAL).toEvent();
		Event rejectedEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).events(validationEvent, rejectedEvent, reviewEvent, approvalEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(ApplicationFormStatus.REVIEW, stage);

	}
	
	@Test
	public void shouldReturnValidationIfRejectedInApprovalPhaseAndPreviousOfApprovalIsValidation() throws ParseException{
		Event validationEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event approvalEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.APPROVAL).toEvent();
		Event rejectedEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/05/05")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.REJECTED).events(validationEvent, approvalEvent, rejectedEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(ApplicationFormStatus.VALIDATION, stage);
	}
	
	@Test
	public void shouldReturnNullIfNotRejected() throws ParseException{
		Event validationEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		Event reviewEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.REVIEW).toEvent();
		Event approvalEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/04/04")).newStatus(ApplicationFormStatus.APPROVAL).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).events(validationEvent, reviewEvent, approvalEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertNull(stage);
	}
	
	@Test
	public void shouldReturnCurrentEventIfOnlyOneEvent() throws ParseException{
		Event validationEvent = new EventBuilder().date(new SimpleDateFormat("yyyy/MM/dd").parse("2012/03/03")).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).events(validationEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(validationEvent.getNewStatus(), stage);
	}
	
	@Test
	public void shouldReturnFirstEventIfOnlyTwoEventsAndLastOneIsRejected() throws ParseException{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = cal.getTime();
		Event rejectedEvent = new EventBuilder().date(tomorrow).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).events(rejectedEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(ApplicationFormStatus.UNSUBMITTED, stage);
	}
	
	@Test
	public void shouldReturnCurrentEventIfOnlyTwoEventsAndLastOneIsNotRejected() throws ParseException{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = cal.getTime();
		Event validationEvent = new EventBuilder().date(tomorrow).newStatus(ApplicationFormStatus.VALIDATION).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().id(1).events(validationEvent).toApplicationForm();
		ApplicationFormStatus stage = applicationsService.getStageComingFrom(application);
		Assert.assertEquals(application.getStatus(), stage);
	}
	

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
