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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.dao.LanguageDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AddressBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.EventBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.PersonalDetailsBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;
import com.zuehlke.pgadmissions.domain.enums.Gender;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class ApplicationFormMappingTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;

	@Test
	public void shouldSaveAndLoadApplicationForm() throws ParseException {

		Date lastUpdatedDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 06 2011 14:05:23");
		ApplicationForm application = new ApplicationForm();
		application.setApplicant(user);
		application.setLastUpdated(lastUpdatedDate);
		application.setProgram(program);		
		application.setStatus(ApplicationFormStatus.APPROVED);
		application.setProjectTitle("bob");

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
	}

	@Test
	public void shouldLoadApplicationFormWithPersonalDetails() throws ParseException {
		Country country1 = new CountryBuilder().code("AA").name("AA").toCountry();
		Country country2 = new CountryBuilder().code("CC").name("CC").toCountry();
		save(country1, country2);

		ApplicationForm application = new ApplicationFormBuilder().applicant(user).program(program).toApplicationForm();

		sessionFactory.getCurrentSession().save(application);
		flushAndClearSession();
		PersonalDetails personalDetails = new PersonalDetailsBuilder().country(country1).dateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse("01/06/1980"))
				.email("email").firstName("firstName").gender(Gender.MALE).lastName("lastname").residenceCountry(country2).requiresVisa(true)
				.englishFirstLanguage(true).phoneNumber("abc").applicationForm(application).toPersonalDetails();

		sessionFactory.getCurrentSession().save(personalDetails);
		flushAndClearSession();

		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(personalDetails, reloadedApplication.getPersonalDetails());

	}

	@Test
	public void shouldSaveAndLoadApplicationFormWithReviewer() {

		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);
		
		application.setReviewers(Arrays.asList(user));

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
		
		Assert.assertEquals(1, reloadedApplication.getReviewers().size());
		Assert.assertTrue(reloadedApplication.getReviewers().contains(user));
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
		LanguageDAO languageDAO = new LanguageDAO(sessionFactory);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Qualification qualification1 = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("").institution("")
				.languageOfStudy(languageDAO.getLanguageById(1)).subject("").isCompleted(CheckedStatus.YES)
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type("").institutionCountry(countriesDAO.getAllCountries().get(0))
				.toQualification();
		Qualification qualification2 = new QualificationBuilder().awardDate(new SimpleDateFormat("yyyy/MM/dd").parse("2011/02/02")).grade("")
				.isCompleted(CheckedStatus.YES).institution("").languageOfStudy(languageDAO.getLanguageById(2)).subject("")
				.startDate(new SimpleDateFormat("yyyy/MM/dd").parse("2006/09/09")).type("").institutionCountry(countriesDAO.getAllCountries().get(0))
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
		reloadedApplication.getNotificationRecords().remove(recordOne);
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
		Event eventOne = new EventBuilder().date(simpleDateFormat.parse("01 12 2011 14:09:26")).newStatus(ApplicationFormStatus.REJECTED).toEvent();
		Event eventTwo = new EventBuilder().date(simpleDateFormat.parse("03 12 2011 14:09:26")).newStatus(ApplicationFormStatus.UNSUBMITTED).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).events(eventOne, eventTwo).toApplicationForm();
		
		save(application);
		Integer eventOneId = eventOne.getId();
		assertNotNull(eventOneId);
		assertNotNull(eventTwo.getId());
		flushAndClearSession();
		
		ApplicationForm reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(3, reloadedApplication.getEvents().size());
		assertTrue(reloadedApplication.getEvents().containsAll(Arrays.asList(eventOne, eventTwo)));
		
		eventOne = (Event) sessionFactory.getCurrentSession().get(Event.class, eventOneId);
		reloadedApplication.getEvents().remove(eventOne);
		save(reloadedApplication);
		flushAndClearSession();
		
		reloadedApplication = (ApplicationForm) sessionFactory.getCurrentSession().get(ApplicationForm.class, application.getId());
		assertEquals(2, reloadedApplication.getEvents().size());
		assertTrue(reloadedApplication.getEvents().containsAll(Arrays.asList(eventTwo)));
		
		assertNull(sessionFactory.getCurrentSession().get(Event.class, eventOneId));

	}
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(user, program);

		flushAndClearSession();
	}

}
