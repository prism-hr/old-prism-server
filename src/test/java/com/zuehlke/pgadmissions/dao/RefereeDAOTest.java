package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;	
	private RefereeDAO refereeDAO;
	private ReminderInterval reminderInterval;

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
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();
		save(application, referee);
		flushAndClearSession();

		Integer id = referee.getId();

		refereeDAO.delete(referee);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Referee.class, id));
	}

	@Test
	public void shouldSaveReferee() throws ParseException {
		Referee referee = new RefereeBuilder().email("email").firstname("name").lastname("last").address1("UK").phoneNumber("hallihallo").build();
		flushAndClearSession();

		refereeDAO.save(referee);
		Assert.assertNotNull(referee.getId());
	}

	@Test
	public void shouldGetRefereeById() {
		Referee referee = new RefereeBuilder().email("email").firstname("name").lastname("last").address1("UK").phoneNumber("hallihallo").build();
		sessionFactory.getCurrentSession().save(referee);
		flushAndClearSession();
		assertEquals(referee.getId(), refereeDAO.getRefereeById(referee.getId()).getId());
	}


	@Test
	public void shouldNotReturnRefereesForInactiveApplicationForms() {
		ApplicationForm unsubmittedApplication = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED)
				.build();
		ApplicationForm approvedApplication = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVED)
				.build();
		ApplicationForm rejectedApplication = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REJECTED)
				.build();
		ApplicationForm withdrawnApplicationForm = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.WITHDRAWN)
				.build();
		save(unsubmittedApplication, approvedApplication, rejectedApplication,withdrawnApplicationForm);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee refereeOne = new RefereeBuilder().declined(false).application(unsubmittedApplication).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").user(user).build();
		
		Referee refereeTwo = new RefereeBuilder().declined(false).application(rejectedApplication).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").user(user).build();
		
		Referee refereeThree = new RefereeBuilder().declined(false).application(approvedApplication).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").user(user).build();
		
		Referee refereeFour = new RefereeBuilder().declined(false).application(withdrawnApplicationForm).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").user(user).build();
		
		save(refereeOne, refereeTwo, refereeThree, refereeFour);
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(refereeOne));
		assertFalse(referees.contains(refereeTwo));
		assertFalse(referees.contains(refereeThree));
		assertFalse(referees.contains(refereeFour));

	}

	@Test
	public void shouldNotReturnRefereesWhoHaveDeclined() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).declined(true).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").user(user).build();		
		save(referee);
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));
	}
	
	@Test
	public void shouldNotReturnRefereesWhoHaveProvidedReference() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").build();
		ReferenceComment reference = new ReferenceCommentBuilder().user(user).comment("comment").application(application).document(document).build();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").reference(reference).user(user).build();			
	
		save(document, reference,referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	
	
	@Test
	public void shouldNotReturnRefereesWhoHaveBeenRemindedInLastWeek() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date threeDaysAgo = DateUtils.addDays(now,-3);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(threeDaysAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").user(user).build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Test
	public void shouldReturnRefereesDueReminders() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date eightDaysAgo = DateUtils.addDays(now,-8);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(eightDaysAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		assertEquals(referee.getId(), refereeDAO.getRefereeById(referee.getId()).getId());
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(listContainsId(referee.getId(), referees));		
	}
	
	@Test
	public void shouldNotReturnRefereesWithNoReminders() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);		
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		
	}
	
	@Test
	public void shouldReturnRefereeForWhichReminderWasSendOneWeekMinus5minAgoForSixDaysInternal() {
		reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(6);
		reminderInterval.setUnit(DurationUnitEnum.DAYS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date oneWeekAgo = DateUtils.addDays(now,-7);
		Date oneWeekMinusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, 5);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(oneWeekMinusFiveMinAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(listContainsId(referee.getId(), referees));			
	}
	
	@Test
	public void shouldNotReturnRefereeReminded6MinutesAgoForOneMinuteReminderInterval() {
		reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(6);
		reminderInterval.setUnit(DurationUnitEnum.MINUTES);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date oneMinuteAgo = DateUtils.addMinutes(now,-1);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(oneMinuteAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
		
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));			
	}
	
	@Test
	public void shouldReturnRefereeReminded2MinutesAgoForOneMinuteReminderInterval() {
		reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.MINUTES);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date twoMinutesAgo = DateUtils.addMinutes(now,-2);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(twoMinutesAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
		
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(listContainsId(referee.getId(), referees));	
	}
	
	@Test
	public void shouldReturnRefereeForWhichReminderWasSendOne6DaysAnd5minAgo() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date sixDaysAgo = DateUtils.addDays(now,-6);
		Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(sixDaysAgo, -5);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(oneWeekPlusFiveMinAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Test
	public void shouldReturnRefereeForWhichReminderWasSendOneWeekPlus5minAgo() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date oneWeekAgo = DateUtils.addMinutes(now, -((int) TimeUnit.MINUTES.convert(7, TimeUnit.DAYS)));
		Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(oneWeekPlusFiveMinAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(listContainsId(referee.getId(), referees));
	}
	
	@Test
	public void shouldNotReturnRefereesForWhichThereIsNoRegisteredUserMapped() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date oneWeekAgo = DateUtils.addDays(now,-7);
		Date oneWeekPlusFiveMinAgo = DateUtils.addMinutes(oneWeekAgo, -5);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(oneWeekPlusFiveMinAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
		
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertFalse(referees.contains(referee));		
		
	}
	
	@Test
	public void shouldReturnRefereesWhoHavenNotProvidedReference() {
		ApplicationForm application = new ApplicationFormBuilder().id(20).program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		ApplicationForm application2 = new ApplicationFormBuilder().id(21).applicant(user).status(ApplicationFormStatus.REJECTED).build();
		save(application, application2);
		
		flushAndClearSession();
		
		Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").build();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee hasRefInApp = new RefereeBuilder().id(1).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fs").firstname("sdsd").jobEmployer("sdfsdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("halliallo").build();			
		
		Referee noRefInApp = new RefereeBuilder().id(2).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("rrwe.fsd").firstname("df").jobEmployer("df").jobTitle("fsdsd").lastname("dsdf").phoneNumber("hahallo").build();			
		
		Referee noRefNoApp = new RefereeBuilder().id(3).application(application2).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("erwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdf").phoneNumber("halliho").build();			
		
		Referee hasRefInApp1 = new RefereeBuilder().id(4).application(application).addressCountry(countriesDAO.getCountryById(2)).address1("sdfsdf")
				.email("rrwe.fsd").firstname("ssdf").jobEmployer("sdfsdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("hallihallo").build();			

		Referee noRefInApp2 = new RefereeBuilder().id(6).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("rrwe.fsd").firstname("dfe").jobEmployer("df").jobTitle("fsdsd").lastname("dsdf").phoneNumber("hahallo").build();			
		
		
		Referee hasRefButNotInApp = new RefereeBuilder().id(5).application(application2).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwesd").firstname("sdf").jobEmployer("sdf").jobTitle("fssd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
		
		save(hasRefButNotInApp, hasRefInApp, hasRefInApp1, noRefInApp, noRefInApp2, noRefNoApp);
		
		ReferenceComment reference = new ReferenceCommentBuilder().document(document)
				.comment("This is a reference comment").suitableForProgramme(false).referee(hasRefInApp1).user(user).application(application)
				.build();

		ReferenceComment referenceOne = new ReferenceCommentBuilder().document(document)
				.comment("This is a reference comment").suitableForProgramme(false).referee(hasRefInApp).user(user).application(application)
				.build();
		
		ReferenceComment referenceTwo = new ReferenceCommentBuilder().document(document)
				.comment("This is a reference comment").suitableForProgramme(false).referee(hasRefButNotInApp).user(user).application(application2)
				.build();
		save(document, reference, referenceOne, referenceTwo);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesWhoDidntProvideReferenceYet(application);
		assertNotNull(referees);
		assertEquals(2, referees.size());
		assertFalse(listContainsId(hasRefInApp.getId(), referees));
		assertTrue(listContainsId(noRefInApp.getId(), referees));		
		assertTrue(listContainsId(noRefInApp2.getId(), referees));		
		assertFalse(listContainsId(noRefNoApp.getId(), referees));		
		assertFalse(listContainsId(hasRefButNotInApp.getId(), referees));		
		assertFalse(listContainsId(hasRefInApp1.getId(), referees));		
	}
	
	@Test
	public void shouldReturnRefereesDueNotifiation() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
		save(application);		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertTrue(listContainsId(referee.getId(), referees));		
	}
	
	@Test
	public void shouldReturnRefereesForNotifiationIfAlreadyNotified() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
		save(application);		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf").lastNotified(new Date())
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	@Test
	public void shouldNotReturnRefereesForNotifiationIfDeclined() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
		save(application);		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").declined(true).build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Test
	public void shouldNotReturnRefereesForNotifiationIfReferenceProvided() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REVIEW).build();
		save(application);		
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		
		Document document = new DocumentBuilder().content("aaa".getBytes()).fileName("hi").build();
		save(document);
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").declined(false).build();			
		
		ReferenceComment reference = new ReferenceCommentBuilder().document(document)
				.comment("This is a reference comment").suitableForProgramme(false).user(user).application(application)
				.referee(referee)
				.build();
	
		save(reference);
		
		flushAndClearSession();
		
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Test
	public void shouldNotReturnRefereesForNotifiationIfAPplicationNotSubmitted() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.UNSUBMITTED).build();
		save(application);		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	
	@Test
	public void shouldNotReturnRefereesForNotifiationIfAPplicationInValidation() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.VALIDATION).build();
		save(application);		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Test
	public void shouldNotReturnRefereesForNotifiationIfAPplicationInAccepted() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.APPROVED).build();
		save(application);		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Test
	public void shouldNotReturnRefereesForNotifiationIfAPplicationRejected() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.REJECTED).build();
		save(application);		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Test
	public void shouldNotReturnRefereesForNotifiationIfAPplicationWithdrawn() {
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).status(ApplicationFormStatus.WITHDRAWN).build();
		save(application);		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().user(user).application(application).addressCountry(countriesDAO.getCountryById(1)).address1("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").build();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueNotification();
		assertNotNull(referees);
		assertFalse(referees.contains(referee));		

	}
	
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

		program = new ProgramBuilder().code("doesntexist").title("another title").build();	
		
		reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		save(user, program);

		flushAndClearSession();
		refereeDAO = new RefereeDAO(sessionFactory);
	}
	
	private boolean listContainsId(Integer id, List<Referee> referees) {
        for (Referee ref : referees) {
            if (ref.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
