package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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
import com.zuehlke.pgadmissions.domain.enums.SearchCategories;

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
	
	@Test
	public void shouldGetAllApplicationsContainingBiologyInTheirNumber(){
		Program program = new ProgramBuilder().id(1).toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().applicationNumber("App_Biology").program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().applicationNumber("ABCD").program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().applicationNumber("BIOLOGY1").program(program).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		
		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormOne, applicationFormTwo, applicationFormThree, applicationFormFour);
			}
		};
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("BiOlOgY", SearchCategories.APPLICATION_CODE, user);
		assertEquals(2, applications.size());
		
	}
	
	@Test
	public void shouldGetApplicationBelongingToProgramWithCodeScienceAndOtherTitle(){
		Program programOne = new ProgramBuilder().code("Program_ZZZZZ_1").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		
		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormOne);
			}
		};
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("zzZZz", SearchCategories.PROGRAMME_NAME, user);
		
		assertEquals(1, applications.size());
		
	}
	
	@Test
	public void shouldGetApplicationBelongingToProgramWithTitleScienceAndOtherCode(){
		Program programOne = new ProgramBuilder().code("empty").title("Program_ZZZZZ_1").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormOne);
			}
		};
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("zzZZz", SearchCategories.PROGRAMME_NAME, user);
		
		assertEquals(1, applications.size());
		
	}
	
	@Test
	public void shouldNotReturnAppIfTermNotInProgrameCodeOrTitle(){
		Program programOne = new ProgramBuilder().code("empty").title("empty").toProgram();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(user).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		
		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormOne);
			}
		};
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("zzZZz", SearchCategories.PROGRAMME_NAME, user);
		
		assertEquals(0, applications.size());
		
	}
	
	@Test
	public void shouldGetApplicationBelongingToApplicantMatchingFirstNameFred(){
		Program programOne = new ProgramBuilder().code("Program_Science_1").title("empty").toProgram();
		RegisteredUser applicant  = new RegisteredUserBuilder().firstName("FredzzZZZZZerick").lastName("Doe").email("email@test.com").username("freddy").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant).status(ApplicationFormStatus.APPROVAL).toApplicationForm();
		
		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormOne);
			}
		};
		
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("zzZZz", SearchCategories.APPLICANT_NAME, user);

		assertEquals(1, applications.size());
		
	}
	
	@Test
	public void shouldGetApplicationBelongingToApplicantMatchingLastName(){
		Program programOne = new ProgramBuilder().code("Program_Science_1").title("empty").toProgram();
		RegisteredUser applicant  = new RegisteredUserBuilder().firstName("Frederick").lastName("FredzzZZZZZerick").email("email@test.com").username("freddy").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormOne);
			}
		};
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("zzZZz", SearchCategories.APPLICANT_NAME, user);
		
		assertEquals(1, applications.size());
		
	}
	
	@Test
	public void shouldNotReturnAppIfTermNotInApplicantNameFirstOrLastName(){
		Program programOne = new ProgramBuilder().code("empty").title("empty").toProgram();
		RegisteredUser applicant  = new RegisteredUserBuilder().firstName("Frederick").lastName("unique").email("email@test.com").username("freddy").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().applicationNumber("ABC").program(programOne).applicant(applicant).status(ApplicationFormStatus.APPROVAL).toApplicationForm();

		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormOne);
			}
		};
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("empty", SearchCategories.APPLICANT_NAME, user);
		
		assertEquals(0, applications.size());
		
	}
	

	@Test
	public void shouldGetAllApplicationsInValidationStage(){
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram(); 
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.VALIDATION).applicationNumber("ABC").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.VALIDATION).applicationNumber("ABCD").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).applicant(user).toApplicationForm();

		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormFour, applicationFormOne, applicationFormThree, applicationFormTwo);
			}
		};
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("validati", SearchCategories.APPLICATION_STATUS, user);
		assertEquals(2, applications.size());
		assertTrue(applications.contains(applicationFormOne));
		assertTrue(applications.contains(applicationFormThree));
		
	}
	
	
	@Test
	public void shouldGetAllApplicationsInApprovalStage(){
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram(); 
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVAL).applicationNumber("ABC").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).applicant(user).toApplicationForm();

		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormFour, applicationFormOne, applicationFormThree, applicationFormTwo);
			}
		};
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("approval", SearchCategories.APPLICATION_STATUS, user);
		assertEquals(1, applications.size());
		assertTrue(applications.contains(applicationFormOne));
		
	}
	
	@Test
	public void shouldGetAllApplicationsInApprovedStage(){
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram(); 
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).applicant(user).toApplicationForm();
		
		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormFour, applicationFormOne, applicationFormThree, applicationFormTwo);
			}
		};
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("approveD", SearchCategories.APPLICATION_STATUS, user);
		assertTrue(applications.contains(applicationFormOne));
		
	}
	
	
	@Test
	public void shouldNotReturnAppIfNoStatusMatching(){
		Program program = new ProgramBuilder().code("empty").title("empty").toProgram(); 
		final ApplicationForm applicationFormOne = new ApplicationFormBuilder().id(1).status(ApplicationFormStatus.APPROVED).applicationNumber("ABC").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormTwo = new ApplicationFormBuilder().id(2).applicationNumber("App_Biology").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormThree = new ApplicationFormBuilder().id(3).status(ApplicationFormStatus.APPROVED).applicationNumber("ABCD").program(program).applicant(user).toApplicationForm();
		final ApplicationForm applicationFormFour = new ApplicationFormBuilder().id(4).applicationNumber("BIOLOGY1").program(program).applicant(user).toApplicationForm();
		
		applicationsService = new ApplicationsService(applicationFormDAOMock){
			@Override
			public List<ApplicationForm> getVisibleApplications(RegisteredUser user){
				return Arrays.asList(applicationFormFour, applicationFormOne, applicationFormThree, applicationFormTwo);
			}
		};
		
		List<ApplicationForm> applications = applicationsService.getAllVisibleAndMatchedApplications("lalala", SearchCategories.APPLICATION_STATUS, user);
		assertEquals(0, applications.size());
		
	}
	
	

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}
}
