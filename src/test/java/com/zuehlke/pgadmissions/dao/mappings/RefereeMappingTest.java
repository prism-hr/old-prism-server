package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.CountryBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

public class RefereeMappingTest extends AutomaticRollbackTestCase {

	private ApplicationForm applicationForm;
	private RegisteredUser refereeUser;
	private RegisteredUser applicant;


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
	public void shouldSaveAndLoadRefereeWithUser() throws Exception {
		RegisteredUser user = new RegisteredUserBuilder().email("testnewemail@onetwo.com").username("testnewemail@onetwo.com").firstName("jane").lastName("u").password("123").toUser();
		
		sessionFactory.getCurrentSession().save(user);
		
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		Referee referee = new RefereeBuilder().application(applicationForm).addressCountry(countriesDAO.getCountryById(1))
				.addressLocation("loc").email("email").user(user).firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname")
				.phoneNumber("phoneNumber").toReferee();
		
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
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		ApplicationForm application = new ApplicationFormBuilder().applicant(applicant).id(2).toApplicationForm();
		
		Country country = new CountryBuilder().code("1").name("nae").toCountry(); 
		save(applicant, application, country);
		
		Referee referee = new RefereeBuilder().application(application).email("email@test.com").firstname("bob")
				.lastname("smith").addressCountry(country).addressLocation("london").jobEmployer("zuhlke").jobTitle("se")
				.messenger("skypeAddress").phoneNumber("hallihallo").user(refereeUser).toReferee();
		
		ReferenceComment referenceComment = new ReferenceCommentBuilder().referee(referee).commentType(CommentType.REFERENCE)
				.comment("This is a reference comment").suitableForProgramme(false).user(refereeUser).application(applicationForm)
				.toReferenceComment();
		save(referee, referenceComment);		
		assertNotNull(referenceComment.getId());
		flushAndClearSession();
		
		Referee reloadedReferee = (Referee) sessionFactory.getCurrentSession().get(Referee.class,referee.getId());	
		assertEquals(referenceComment, reloadedReferee.getReference());
		
	}

	@Before
	public void setUp() {
		super.setUp();

		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();
		
		save(program);

		applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		refereeUser = new RegisteredUserBuilder().firstName("hanna").lastName("hoopla").email("hoopla@test.com").username("username1")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		save(applicant, refereeUser);
		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}
}
