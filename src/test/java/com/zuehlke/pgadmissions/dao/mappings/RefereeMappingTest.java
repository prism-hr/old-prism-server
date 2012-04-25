package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Reference;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class RefereeMappingTest extends AutomaticRollbackTestCase {

	private ApplicationForm applicationForm;


	@Test
	public void shouldSaveAndLoadReferee() throws Exception {
		Date lastNotified = new SimpleDateFormat("dd MM yyyy").parse("01 05 2012");
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(applicationForm).addressCountry(countriesDAO.getCountryById(1)).addressLocation("loc")
				.email("email").firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname").phoneNumber("phoneNumber").declined(true).lastNotified(lastNotified)
				.toReferee();

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

		assertEquals(referee.getApplication(), reloadedReferee.getApplication());
		assertEquals(referee.getEmail(), reloadedReferee.getEmail());
		assertEquals(referee.getFirstname(), reloadedReferee.getFirstname());
		assertEquals(referee.getJobEmployer(), reloadedReferee.getJobEmployer());
		assertEquals(referee.getJobTitle(), reloadedReferee.getJobTitle());
		assertEquals(referee.getLastname(), reloadedReferee.getLastname());

		assertEquals(referee.getPhoneNumber(), reloadedReferee.getPhoneNumber());
		assertTrue(reloadedReferee.isDeclined());
		assertEquals(lastNotified, reloadedReferee.getLastNotified());

	}

	

	@Test
	public void shouldSaveAndLoadRefereeWithReference() throws Exception {
		Reference reference = new ReferenceBuilder().toReference();

		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().reference(reference).application(applicationForm).addressCountry(countriesDAO.getCountryById(1))
				.addressLocation("loc").email("email").firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname")
				.phoneNumber("phoneNumber").toReferee();

		sessionFactory.getCurrentSession().save(referee);
		assertNotNull(reference.getId());
		Integer referenceId = reference.getId();
		flushAndClearSession();

		Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		assertEquals(reference, reloadedReferee.getReference());
		Reference reloadedReference = (Reference) sessionFactory.getCurrentSession().get(Reference.class,referenceId);
		assertEquals(reloadedReferee, reloadedReference.getReferee());

		reloadedReferee.setReference(null);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);
		flushAndClearSession();

		assertNull(sessionFactory.getCurrentSession().get(Reference.class, referenceId));

		reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		Reference reference2 = new ReferenceBuilder().toReference();
		reloadedReferee.setReference(reference2);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);
		flushAndClearSession();		
		
		assertNotNull(reference2.getId());		
		Integer reference2Id = reference2.getId();
		
		reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		sessionFactory.getCurrentSession().delete(reloadedReferee);
		flushAndClearSession();		
		assertNull(sessionFactory.getCurrentSession().get(Reference.class, reference2Id));
		

	}
	
	@Test
	public void shouldSaveAndLoadRefereeWithUser() throws Exception {
		Reference reference = new ReferenceBuilder().toReference();
		RegisteredUser user = new RegisteredUserBuilder().email("testnewemail@onetwo.com").username("testnewemail@onetwo.com").firstName("jane").lastName("u").password("123").toUser();
		
		sessionFactory.getCurrentSession().save(user);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().reference(reference).application(applicationForm).addressCountry(countriesDAO.getCountryById(1))
				.addressLocation("loc").email("email").user(user).firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname")
				.phoneNumber("phoneNumber").toReferee();
		
		sessionFactory.getCurrentSession().save(referee);
		assertNotNull(reference.getId());
		Integer referenceId = reference.getId();
		flushAndClearSession();
		
		Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		assertEquals(reference, reloadedReferee.getReference());
		Reference reloadedReference = (Reference) sessionFactory.getCurrentSession().get(Reference.class,referenceId);
		assertEquals(reloadedReferee, reloadedReference.getReferee());
		
		reloadedReferee.setReference(null);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);
		flushAndClearSession();
		
		assertNull(sessionFactory.getCurrentSession().get(Reference.class, referenceId));
		
	}

	@Before
	public void setUp() {
		super.setUp();

		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		
		save(program);

		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
}
