package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.util.Calendar;
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
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.DocumentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApprovalStatus;
import com.zuehlke.pgadmissions.domain.enums.SubmissionStatus;

public class RefereeDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;
	private Project project;
	private RefereeDAO refereeDAO;

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		RefereeDAO refereeDAO = new RefereeDAO();
		refereeDAO.getRefereeById(1);
	}

	@Test
	public void shouldDeleteReferee() {

		ApplicationForm application = new ApplicationForm();
		application.setProject(project);
		application.setApplicant(user);
		application.setSubmissionStatus(SubmissionStatus.SUBMITTED);
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
		ApplicationForm unsubmittedApplication = new ApplicationFormBuilder().project(project).applicant(user).submissionStatus(SubmissionStatus.UNSUBMITTED)
				.toApplicationForm();
		ApplicationForm decidedApplication = new ApplicationFormBuilder().project(project).applicant(user).submissionStatus(SubmissionStatus.SUBMITTED)
				.approvedSatus(ApprovalStatus.APPROVED).toApplicationForm();
		save(unsubmittedApplication, decidedApplication);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee refereeOne = new RefereeBuilder().application(unsubmittedApplication).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();
		Referee refereeTwo = new RefereeBuilder().application(decidedApplication).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf")
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
		ApplicationForm application = new ApplicationFormBuilder().project(project).applicant(user).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
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
		ApplicationForm application = new ApplicationFormBuilder().project(project).applicant(user).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
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
		ApplicationForm application = new ApplicationFormBuilder().project(project).applicant(user).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
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
		ApplicationForm application = new ApplicationFormBuilder().project(project).applicant(user).submissionStatus(SubmissionStatus.SUBMITTED).toApplicationForm();
		save(application);
		Date now = Calendar.getInstance().getTime();		
		Date eightDaysAgo = DateUtils.addDays(now,-8);
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1)).addressLocation("sdfsdf").lastNotified(eightDaysAgo)
				.email("errwe.fsd").firstname("sdsdf").jobEmployer("sdfsdf").jobTitle("fsdsd").lastname("fsdsdf").phoneNumber("hallihallo").toReferee();			
	
		save(referee);
		
		flushAndClearSession();
		List<Referee> referees = refereeDAO.getRefereesDueAReminder();
		assertNotNull(referees);
		assertTrue(referees.contains(referee));		

	}
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(user, program, project);

		flushAndClearSession();
		refereeDAO = new RefereeDAO(sessionFactory);
	}
}
