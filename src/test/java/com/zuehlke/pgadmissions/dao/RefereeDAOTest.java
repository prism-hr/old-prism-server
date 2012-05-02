package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;	
	private RefereeDAO refereeDAO;

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		RefereeDAO refereeDAO = new RefereeDAO();
		refereeDAO.getRefereeById(1);
	}

	@Test
	public void shouldDeleteReferee() {

		ApplicationForm application = new ApplicationForm();
		application.setProgram(program);
		application.setApplicant(user);
	
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();
		save(application, referee);
		flushAndClearSession();

		Integer id = referee.getId();

		refereeDAO.delete(referee);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Referee.class, id));
	}

	@Test
	public void shouldSaveReferee() throws ParseException {
		Referee referee = new RefereeBuilder().email("email").firstname("name").lastname("last").addressLocation("UK").phoneNumber("hallihallo").toReferee();
		flushAndClearSession();

		refereeDAO.save(referee);
		Assert.assertNotNull(referee.getId());
	}

	@Test
	public void shouldGetRefereeById() {
		Referee referee = new RefereeBuilder().email("email").firstname("name").lastname("last").addressLocation("UK").phoneNumber("hallihallo").toReferee();

		sessionFactory.getCurrentSession().save(referee);
		flushAndClearSession();

		assertEquals(referee, refereeDAO.getRefereeById(referee.getId()));

	}

	@Test
	public void shouldGetProgramByActivationCode() {
		Referee referee = new RefereeBuilder().activationCode("abcde").email("email").firstname("name").lastname("last").addressLocation("UK")
				.phoneNumber("hallihallo").toReferee();

		sessionFactory.getCurrentSession().save(referee);
		flushAndClearSession();

		assertEquals(referee, refereeDAO.getRefereeByActivationCode("abcde"));

	}

	@Test
	public void shouldNotReturnRefereesForInactiveApplicationForms() {
		ApplicationForm unsubmittedApplication = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED)
				.toApplicationForm();
		ApplicationForm decidedApplication = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVED)
				.toApplicationForm();
		save(unsubmittedApplication, decidedApplication);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee refereeOne = new RefereeBuilder().declined(false).application(unsubmittedApplication).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();
		Referee refereeTwo = new RefereeBuilder().declined(false).application(decidedApplication).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();
		save(refereeOne, refereeTwo);
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(refereeOne));
		assertFalse(referees.contains(refereeTwo));

	}

	@Test
	public void shouldNotReturnRefereesWhoHaveDeclined() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).declined(true).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();		
		save(referee);
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));
		

	}
	
	@Test
	public void shouldNotReturnRefereesWhoHaveProvidedReference() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").toDocument();
		Reference reference = new ReferenceBuilder().document(document).toReference();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").reference(reference).toReferee();			
	
		save(document, reference,referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	
	
	@Test
	public void shouldNotReturnRefereesWhoHaveBeenRemindedInLastWeek() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date threeDaysAgo = DateUtils.addDays(now,-3);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf").lastNotified(threeDaysAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Test
	public void shouldReturnRefereesDueReminders() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date eightDaysAgo = DateUtils.addDays(now,-8);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).toUser();
		Referee referee = new RefereeBuilder().user(refereeUser).application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf").lastNotified(eightDaysAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(referees.contains(referee));		

	}
	
	@Test
	public void shouldReturnRefereesWithNoReminders() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);		
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).toUser();
		Referee referee = new RefereeBuilder().user(refereeUser).application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(referees.contains(referee));		

	}
	@Test
	public void shouldReturnRefereeForWhichReminderWasSendOneWeekMinus5minAgo() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date oneWeekAgo = DateUtils.addDays(now,-7);
		Date oneWeekMinusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, 5);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).toUser();
		Referee referee = new RefereeBuilder().user(refereeUser).application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf").lastNotified(oneWeekMinusFiveMinAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(referees.contains(referee));			
	}
	
	@Test
	public void shouldReturnRefereeForWhichReminderWasSendOne6DaysAnd5minAgo() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date sixDaysAgo = DateUtils.addDays(now,-6);
		Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(sixDaysAgo, -5);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf").lastNotified(oneWeekPlusFiveMinAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	
	@Test
	public void shouldReturnRefereeForWhichReminderWasSendOneWeekPlus5minAgo() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date oneWeekAgo = DateUtils.addDays(now,-7);
		Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		RegisteredUser refereeUser = new RegisteredUserBuilder().id(1).toUser();
		Referee referee = new RefereeBuilder().user(refereeUser).application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf").lastNotified(oneWeekPlusFiveMinAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(referees.contains(referee));		

	}
	@Test
	public void shouldNotReturnRefereesForWhichThereIsNoRegisteredUserMapped() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date oneWeekAgo = DateUtils.addDays(now,-7);
		Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf").lastNotified(oneWeekPlusFiveMinAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();			
		
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertFalse(referees.contains(referee));		
		
	}
	
	@Test
	public void shouldReturnRefereesWhoHavenNotProvidedReference() {
		ApplicationForm application = new ApplicationFormBuilder().id(20).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).toApplicationForm();
		ApplicationForm application2 = new ApplicationFormBuilder().id(21).applicant(user).status(ApplicationFormStatus.REJECTED).toApplicationForm();
		save(application, application2);
		flushAndClearSession();
		Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").toDocument();
		Reference reference = new ReferenceBuilder().document(document).toReference();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee hasRefInApp = new RefereeBuilder().id(1).application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwe.fs").firstname("sdsd").jobEmployer("sdfsdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("halliallo").reference(reference).toReferee();			
		
		Referee noRefInApp = new RefereeBuilder().id(2).application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("rrwe.fsd").firstname("df").jobEmployer("df").jobTitle("fsdsd").lastname("dsdf").phoneNumber("hahallo").toReferee();			
		
		Referee noRefNoApp = new RefereeBuilder().id(3).application(application2).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("erwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdf").phoneNumber("halliho").toReferee();			
		
		Referee hasRefInApp1 = new RefereeBuilder().id(4).application(application).addressCountry(countriesDAO.getCountryById(2)).addressLocation("sdfsdf")
				.email("rrwe.fsd").firstname("ssdf").jobEmployer("sdfsdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("hallihallo").reference(reference).toReferee();			
		
		Referee noRefInApp2 = new RefereeBuilder().id(6).application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("rrwe.fsd").firstname("df").jobEmployer("df").jobTitle("fsdsd").lastname("dsdf").phoneNumber("hahallo").toReferee();			
		
		Referee hasRefButNotInApp = new RefereeBuilder().id(5).application(application2).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwesd").firstname("sdf").jobEmployer("sdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("hallihallo").reference(reference).toReferee();			
		
		save(document, reference,hasRefInApp, noRefInApp, noRefNoApp, hasRefButNotInApp, hasRefInApp1, noRefInApp2);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesWhoDidntProvideReferenceYet(application);
		assertNotNull(referees);
		assertEquals(2, referees.size());
		assertFalse(referees.contains(hasRefInApp));		
		assertTrue(referees.contains(noRefInApp));		
		assertTrue(referees.contains(noRefInApp2));		
		assertFalse(referees.contains(noRefNoApp));		
		assertFalse(referees.contains(hasRefButNotInApp));		
		assertFalse(referees.contains(hasRefInApp1));		
		
	}
	
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();		
		save(user, program);

		flushAndClearSession();
		refereeDAO = new RefereeDAO(sessionFactory);
	}
}
