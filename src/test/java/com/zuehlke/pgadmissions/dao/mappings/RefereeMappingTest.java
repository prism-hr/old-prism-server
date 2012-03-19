package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Messenger;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Project;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Telephone;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.MessengerBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProjectBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.TelephoneBuilder;
import com.zuehlke.pgadmissions.domain.enums.PhoneType;


public class RefereeMappingTest extends AutomaticRollbackTestCase {

	private ApplicationForm applicationForm;

	@Test
	public void shouldSaveAndLoadReferee() throws Exception {

		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(applicationForm).addressCountry(countriesDAO.getCountryById(1)).addressLocation("loc").addressPostcode("pos").email("email").firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname").relationship("rel").toReferee();
		
		sessionFactory.getCurrentSession().save(referee);
		assertNotNull(referee.getId());
		Integer id = referee.getId();
		Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, id);

		assertSame(referee, reloadedReferee);

		flushAndClearSession();
		reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, id);

		assertNotSame(referee, reloadedReferee);
		assertEquals(referee, reloadedReferee);

		assertEquals(referee.getAddressCountry(), reloadedReferee.getAddressCountry());
		assertEquals(referee.getAddressLocation(), reloadedReferee.getAddressLocation());
		assertEquals(referee.getAddressPostcode(), reloadedReferee.getAddressPostcode());
		assertEquals(referee.getApplication(), reloadedReferee.getApplication());
		assertEquals(referee.getEmail(), reloadedReferee.getEmail());
		assertEquals(referee.getFirstname(), reloadedReferee.getFirstname());
		assertEquals(referee.getJobEmployer(), reloadedReferee.getJobEmployer());
		assertEquals(referee.getJobTitle(), reloadedReferee.getJobTitle());
		assertEquals(referee.getLastname(), reloadedReferee.getLastname());
		assertEquals(referee.getRelationship(), reloadedReferee.getRelationship());

	}
	
	@Test
	public void shouldSaveAndLoadRefereeWithPhoneNumbers() throws Exception {
		Telephone telephone1 = new TelephoneBuilder().telephoneNumber("abc").telephoneType(PhoneType.MOBILE).toTelephone();
		Telephone telephone2 = new TelephoneBuilder().telephoneNumber("abc").telephoneType(PhoneType.HOME).toTelephone();
		Telephone telephone3 = new TelephoneBuilder().telephoneNumber("abc").telephoneType(PhoneType.WORK).toTelephone();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().phoneNumbers(telephone1, telephone2).application(applicationForm).addressCountry(countriesDAO.getCountryById(1)).addressLocation("loc").addressPostcode("pos").email("email").firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname").relationship("rel").toReferee();
		
		sessionFactory.getCurrentSession().save(referee);
		assertNotNull(telephone1.getId());
		assertNotNull(telephone2.getId());
		flushAndClearSession();
		Referee  reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		assertEquals(2, reloadedReferee.getPhoneNumbers().size());
		assertTrue(reloadedReferee.getPhoneNumbers().containsAll(Arrays.asList(telephone1, telephone2)));

		reloadedReferee.getPhoneNumbers().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);

		flushAndClearSession();
		reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		assertEquals(1, reloadedReferee.getPhoneNumbers().size());
		assertTrue(reloadedReferee.getPhoneNumbers().containsAll(Arrays.asList(telephone1)));

		reloadedReferee.getPhoneNumbers().add(telephone3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);
		flushAndClearSession();
		
		reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		assertEquals(2, reloadedReferee.getPhoneNumbers().size());
		assertTrue(reloadedReferee.getPhoneNumbers().containsAll(Arrays.asList(telephone1, telephone3)));

	}
	
	@Test
	public void shouldSaveAndLoadRefereeWithMessengers() throws Exception {
		Messenger messenger1 = new MessengerBuilder().messengerAddress("john17").toMessenger();
		Messenger messenger2 = new MessengerBuilder().messengerAddress("john17").toMessenger();
		Messenger messenger3 = new MessengerBuilder().messengerAddress("john17").toMessenger();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().messengers(messenger1, messenger2).application(applicationForm).addressCountry(countriesDAO.getCountryById(1)).addressLocation("loc").addressPostcode("pos").email("email").firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname").relationship("rel").toReferee();
		
		sessionFactory.getCurrentSession().save(referee);
		assertNotNull(messenger1.getId());
		assertNotNull(messenger2.getId());
		flushAndClearSession();
		Referee  reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		assertEquals(2, reloadedReferee.getMessengers().size());
		assertTrue(reloadedReferee.getMessengers().containsAll(Arrays.asList(messenger1, messenger2)));
		
		reloadedReferee.getMessengers().remove(1);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);
		
		flushAndClearSession();
		reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		assertEquals(1, reloadedReferee.getMessengers().size());
		assertTrue(reloadedReferee.getMessengers().containsAll(Arrays.asList(messenger1)));
		
		reloadedReferee.getMessengers().add(messenger3);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);
		flushAndClearSession();
		
		reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		assertEquals(2, reloadedReferee.getMessengers().size());
		assertTrue(reloadedReferee.getMessengers().containsAll(Arrays.asList(messenger1, messenger3)));
		
	}
	
	@Before
	public void setUp() {
		super.setUp();

		Program program = new ProgramBuilder().code("doesntexist").description("blahblab").title("another title").toProgram();
		Project project = new ProjectBuilder().code("neitherdoesthis").description("hello").title("title two").program(program).toProject();
		save(program, project);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).project(project).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
}
