package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class RefereeMappingTest extends AutomaticRollbackTestCase {

	private ApplicationForm applicationForm;
	private RegisteredUser refereeUser;
	private RegisteredUser applicant;


	@Test
	public void shouldSaveAndLoadReferee() throws Exception {
		Date lastNotified = new SimpleDateFormat("dd MM yyyy").parse("01 05 2012");
		
		Country country = new CountryBuilder().code("FF").enabled(true).name("FF").build();
		
		save(country);
		flushAndClearSession();
		
        Referee referee = new RefereeBuilder().application(applicationForm)
                .addressCountry(country).address1("loc").email("email").firstname("name")
                .jobEmployer("emplo").jobTitle("titl").lastname("lastname").phoneNumber("phoneNumber").declined(true)
                .lastNotified(lastNotified).build();

		save(referee);
		flushAndClearSession();
		
		assertNotNull(referee.getId());
		Integer id = referee.getId();
		Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, id);

		assertNotSame(referee, reloadedReferee);
		assertEquals(referee.getId(), reloadedReferee.getId());

		assertEquals(referee.getAddressLocation().getCountry().getId(), reloadedReferee.getAddressLocation().getCountry().getId());
		assertEquals(referee.getAddressLocation().getId(), reloadedReferee.getAddressLocation().getId());

		assertEquals(referee.getApplication().getId(), reloadedReferee.getApplication().getId());
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
	public void shouldSaveAndLoadRefereeWithUser() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().email("testnewemail@onetwo.com").username("testnewemail@onetwo.com").firstName("jane").lastName("u").password("123").build();
		
		sessionFactory.getCurrentSession().save(user);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(applicationForm).addressCountry(countriesDAO.getCountryById(1))
				.address1("loc").email("email").user(user).firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname")
				.phoneNumber("phoneNumber").build();
		
		sessionFactory.getCurrentSession().save(referee);
		flushAndClearSession();
		
		Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class, referee.getId());
		
		reloadedReferee.setReference(null);
		sessionFactory.getCurrentSession().saveOrUpdate(reloadedReferee);
		flushAndClearSession();
		
		
	}
	
	@Test
	public void shoulLoadReferenceWithReferee() throws ParseException{
		
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("user").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		ApplicationForm application = new ApplicationFormBuilder().applicant(applicant).id(2).build();
		
		Country country = new CountryBuilder().name("nae").code("NA").enabled(true).build(); 
		save(applicant, application, country);
		
		Referee referee = new RefereeBuilder().application(application).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry(country).address1("london").jobEmployer("zuhlke").jobTitle("se")
				.messenger("skypeAddress").phoneNumber("hallihallo").user(refereeUser).build();
		
		ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee)
				.comment("This is a reference comment").suitableForProgramme(false).user(refereeUser).application(applicationForm)
				.build();
		save(referee, referenceComment);		
		assertNotNull(referenceComment.getId());
		flushAndClearSession();
		
		Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class,referee.getId());	
		assertEquals(referenceComment.getId(), reloadedReferee.getReference().getId());
		
	}

	@Before
	public void prepare() {
		Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
		
		save(program);

		applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		refereeUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("username1")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		save(applicant, refereeUser);
		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
		save(applicationForm);
		flushAndClearSession();
	}
}
