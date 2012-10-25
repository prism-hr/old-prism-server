package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.DomicileDAO;
import com.zuehlke.pgadmissions.dao.QualificationTypeDAO;
import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.DomicileBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;
import com.zuehlke.pgadmissions.domain.enums.Title;

public class ApplicationFormMappingTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;
	private RegisteredUser reviewerUser;
	private RegisteredUser interviewerUser;
	private RegisteredUser applicationAdmin;
	private RegisteredUser approver;

	@Test
	public void shouldSaveAndLoadApplicationForm() throws ParseException {

		Date lastUpdatedDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 06 2011 14:05:23");
		ApplicationForm application = new ApplicationForm();
		application.setApplicant(user);
		application.setLastUpdated(lastUpdatedDate);
		application.setProgram(program);		
		application.setStatus(ApplicationFormStatus.APPROVED);
		application.setProjectTitle("bob");
		application.setApplicationAdministrator(applicationAdmin);
		application.setApplicationNumber("ABC");
		application.setResearchHomePage("researchHomePage");
		application.setPendingApprovalRestart(true);
		assertNotNull(application.getPersonalDetails());
		assertNull(application.getId());

		sessionFactory.getCurrentSession().save(application);

		assertNotNull(application.getId());
		Integer id = application.getId();
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertSame(application, reloadedApplication);

		flushAndClearSession();

		reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertNotSame(application, reloadedApplication);
		assertEquals(application, reloadedApplication);

		assertEquals(user, reloadedApplication.getApplicant());

		assertEquals(program, reloadedApplication.getProgram());
		
		assertEquals(ApplicationFormStatus.APPROVED, reloadedApplication.getStatus());
		assertEquals("bob", reloadedApplication.getProjectTitle());
		assertNotNull(application.getPersonalDetails());
		assertEquals(lastUpdatedDate, application.getLastUpdated());
		assertNull(application.getPersonalDetails().getId());
		assertEquals(applicationAdmin, application.getApplicationAdministrator());
		assertEquals("ABC", application.getApplicationNumber());
		assertEquals("http://researchHomePage", application.getResearchHomePage());
		assertTrue(application.isPendingApprovalRestart());
	}

	@Test
	public void shouldLoadApplicationFormWithPersonalDetails() throws ParseException {
		Country country1 = new CountryBuilder().name("AA").code("AA").enabled(true).toCountry();
		Country country2 = new CountryBuilder().name("CC").code("CC").enabled(true).toCountry();
		Domicile country3 = new DomicileBuilder().name("DD").code("DD").enabled(true).toDomicile();
		save(country1, country2, country3);

		ApplicationForm application = new ApplicationFormBuilder().applicant(user).program(program).toApplicationForm();

		sessionFactory.getCurrentSession().save(application);
		flushAndClearSession();
		PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country1).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.email("email").firstName("firstName").title(Title.MR).gender(Gender.MALE).lastName("lastname").residenceDomicile(country3).requiresVisa(true)
				.englishFirstLanguage(true).phoneNumber("abc").applicationForm(application).toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		flushAndClearSession();

		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(personalDetails, reloadedApplication.getPersonalDetails());

	}

	
	@Test
	public void shouldLoadApplicationFormWithInterview() throws ParseException {
		
		ApplicationForm application = new ApplicationFormBuilder().applicant(user).program(program).toApplicationForm();
		
		sessionFactory.getCurrentSession().save(application);
		flushAndClearSession();
		Interview interview = new InterviewBuilder().application(application).lastNotified(new Date()).furtherDetails("tba").locationURL("pgadmissions").toInterview();
		
		sessionFactory.getCurrentSession().save(interview);
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(interview, reloadedApplication.getInterviews().get(0));
		
	}
	
	@Test
	public void shouldSaveAndLoadApplicationFormWithAddress() {
		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);

		Address addressOne = new AddressBuilder().country(countriesDAO.getAllCountries().get(0)).location("london").toAddress();
		Address addressTwo = new AddressBuilder().country(countriesDAO.getAllCountries().get(0)).location("london").toAddress();

		application.setCurrentAddress(addressOne);
		application.setContactAddress(addressTwo);

		save(application);
		assertNotNull(addressOne.getId());
		assertNotNull(addressTwo.getId());
		flushAndClearSession();

		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(addressOne, reloadedApplication.getCurrentAddress());
		assertEquals(addressTwo, reloadedApplication.getContactAddress());
	}

	@Test
	public void shouldLoadApplicationFormWithCVAndPersonalStatement() {

		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);
		Document cv = new DocumentBuilder().fileName("bob").type(DocumentType.CV).content("aaa!".getBytes()).toDocument();
		Document personalStatement = new DocumentBuilder().fileName("bob").type(DocumentType.PERSONAL_STATEMENT).content("aaa!".getBytes()).toDocument();
		save(cv, personalStatement);
		flushAndClearSession();
		application.setCv(cv);
		application.setPersonalStatement(personalStatement);

		sessionFactory.getCurrentSession().save(application);
		flushAndClearSession();

		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());

		assertEquals(cv, reloadedApplication.getCv());
		assertEquals(personalStatement, application.getPersonalStatement());
	}

	@Test
	public void shouldLoadApplicationFormWithComments() {

		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);

		sessionFactory.getCurrentSession().save(application);
		Integer id = application.getId();
		flushAndClearSession();

		Comment commentOne = new CommentBuilder().application(application).comment("comment1").user(user).toComment();
		Comment commentTwo = new CommentBuilder().application(application).comment("comment2").user(user).toComment();
		save(commentOne, commentTwo);

		flushAndClearSession();

		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertEquals(2, reloadedApplication.getApplicationComments().size());
		assertTrue(reloadedApplication.getApplicationComments().containsAll(Arrays.asList(commentOne, commentTwo)));
	}

	@Test
	public void shouldSaveQualificationsWithApplication() throws ParseException {

		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);

		// sessionFactory.getCurrentSession().save(application);
		// Integer id = application.getId();
		// flushAndClearSession();
		QualificationTypeDAO qualificationTypeDAO = new QualificationTypeDAO(sessionFactory);
		 DomicileDAO domicileDAO = new DomicileDAO(sessionFactory);
		Qualification qualification1 = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").institution("")
				.languageOfStudy("Abkhazian").subject("").isCompleted(CheckedStatus.YES)
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type(qualificationTypeDAO.getAllQualificationTypes().get(0)).institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0))
				.toQualification();
		Qualification qualification2 = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("")
				.isCompleted(CheckedStatus.YES).institution("").languageOfStudy("Achinese").subject("")
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type(qualificationTypeDAO.getAllQualificationTypes().get(0)).institutionCountry(domicileDAO.getAllEnabledDomiciles().get(0))
				.toQualification();

		application.getQualifications().addAll(Arrays.asList(qualification1, qualification2));

		sessionFactory.getCurrentSession().saveOrUpdate(application);
		flushAndClearSession();

		Integer id = application.getId();
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, id);
		assertEquals(2, reloadedApplication.getQualifications().size());

	}
	
	@Test
	public void shouldSaveAndLoadNotificationRecordsWithApplication() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
		NotificationRecord recordOne = new NotificationRecordBuilder().notificationDate(simpleDateFormat.parse("01 12 2011 14:09:26")).notificationType(NotificationType.UPDATED_NOTIFICATION).toNotificationRecord();
		NotificationRecord recordTwo = new NotificationRecordBuilder().notificationDate(simpleDateFormat.parse("03 12 2011 14:09:26")).notificationType(NotificationType.VALIDATION_REMINDER).toNotificationRecord();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).notificationRecords(recordOne, recordTwo).toApplicationForm();
		
		save(application);
		Integer recordOneId = recordOne.getId();
		assertNotNull(recordOneId);
		assertNotNull(recordTwo.getId());
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(2, reloadedApplication.getNotificationRecords().size());
		assertTrue(reloadedApplication.getNotificationRecords().containsAll(Arrays.asList(recordOne, recordTwo)));
		
		recordOne = (NotificationRecord) sessionFactory.getCurrentSession().get(NotificationRecord.class, recordOneId);
		reloadedApplication.removeNotificationRecord(recordOne);
		save(reloadedApplication);
		flushAndClearSession();
		
		reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(1, reloadedApplication.getNotificationRecords().size());
		assertTrue(reloadedApplication.getNotificationRecords().containsAll(Arrays.asList(recordTwo)));
		
		assertNull(sessionFactory.getCurrentSession().get(NotificationRecord.class, recordOneId));

	}
	
	@Test
	public void shouldSaveAndLoadEventsWithApplication() throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy hh:mm:ss");
		StateChangeEvent eventOne = new StateChangeEventBuilder().date(simpleDateFormat.parse("01 12 2011 14:09:26")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		StateChangeEvent eventTwo = new StateChangeEventBuilder().date(simpleDateFormat.parse("03 12 2011 14:09:26")).newStatus(ApplicationFormStatus.UNSUBMITTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).events(eventOne, eventTwo).toApplicationForm();
		
		save(application);
		Integer eventOneId = eventOne.getId();
		assertNotNull(eventOneId);
		assertNotNull(eventTwo.getId());
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(2, reloadedApplication.getEvents().size());
		assertTrue(reloadedApplication.getEvents().containsAll(Arrays.asList(eventOne, eventTwo)));
		
		eventOne = (StateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, eventOneId);
		reloadedApplication.getEvents().remove(eventOne);
		save(reloadedApplication);
		flushAndClearSession();
		
		reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(1, reloadedApplication.getEvents().size());
		assertTrue(reloadedApplication.getEvents().containsAll(Arrays.asList(eventTwo)));
		
		assertNull(sessionFactory.getCurrentSession().get(StateChangeEvent.class, eventOneId));

	}
	
	
	@Test
	public void shouldLoadInterviewsForApplicationForm() throws ParseException, InterruptedException {
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).toApplicationForm();		
		save(application);
		
		Interview interviewOne = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).toInterviewer()).application(application).toInterview();
		save(interviewOne);

		Interview interviewTwo = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).toInterviewer()).application(application).toInterview();
		save(interviewTwo);

		
		Interview interviewTrhee = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).toInterviewer()).application(application).toInterview();
		save(interviewTrhee);

		
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(3, reloadedApplication.getInterviews().size());
		assertTrue(reloadedApplication.getInterviews().containsAll(Arrays.asList(interviewOne, interviewTwo, interviewTrhee)));
		
	}
	
	
	@Test
	public void shouldSaveAndLoadLatestInterview() throws ParseException, InterruptedException {
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).toApplicationForm();		
		save(application);
		
		Interview interview = new InterviewBuilder().interviewers(new InterviewerBuilder().user(interviewerUser).toInterviewer()).application(application).toInterview();
		save(interview);
		
		application.setLatestInterview(interview);
		save(application);
		
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		
		assertEquals(interview, reloadedApplication.getLatestInterview());
		
	}
	
	

	@Test
	public void shouldLoadReveiwRoundForApplicationForm() throws ParseException, InterruptedException {
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).toApplicationForm();		
		save(application);
		
		ReviewRound reviewRoundOne = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).toReviewer()).application(application).toReviewRound();
		save(reviewRoundOne);

		ReviewRound reviewRoundTwo = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).toReviewer()).application(application).toReviewRound();
		save(reviewRoundTwo);

		
		ReviewRound reviewRoundTrhee = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).toReviewer()).application(application).toReviewRound();
		save(reviewRoundTrhee);

		
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(3, reloadedApplication.getReviewRounds().size());
		assertTrue(reloadedApplication.getReviewRounds().containsAll(Arrays.asList(reviewRoundOne, reviewRoundTwo, reviewRoundTrhee)));
		
	}
	
	@Test
	public void shouldSaveAndLoadLatestReviewRound() throws ParseException, InterruptedException {
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).toApplicationForm();		
		save(application);
		
		ReviewRound reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(reviewerUser).toReviewer()).application(application).toReviewRound();
		save(reviewRound);
		
		application.setLatestReviewRound(reviewRound);
		save(application);
		
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		
		assertEquals(reviewRound, reloadedApplication.getLatestReviewRound());
		
	}
	
	@Test
	public void shouldSaveAndLoadRejection() throws ParseException, InterruptedException {
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).toApplicationForm();		
		save(application);
		
		RejectReasonDAO rejectReasonDAO = new RejectReasonDAO(sessionFactory);
		RejectReason rejectReason = rejectReasonDAO.getAllReasons().get(0);
		Rejection rejection = new RejectionBuilder().includeProspectusLink(true).rejectionReason(rejectReason).toRejection();
		
		application.setRejection(rejection);
		save(application);
		
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		
		assertEquals(rejection, reloadedApplication.getRejection());
		
		
	}
	@Test
	public void shouldSaveAndLoadApplicationFormWithApproverRequestingRestart() {
		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);
		application.setApproverRequestedRestart(approver);
		
	
		save(application);
		
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		
		assertEquals(approver, reloadedApplication.getApproverRequestedRestart());
		
		
	}
	
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		reviewerUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("hoopla").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
	
		interviewerUser = new RegisteredUserBuilder().firstName("brad").lastName("brady").email("brady@test.com").username("brady").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		applicationAdmin = new RegisteredUserBuilder().firstName("joan").lastName("arc").email("act@test.com").username("arc").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		approver = new RegisteredUserBuilder().firstName("het").lastName("get").email("het@test.com").username("hed").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		
		
		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(user, reviewerUser, program, interviewerUser, applicationAdmin, approver);

		flushAndClearSession();
	}

}
