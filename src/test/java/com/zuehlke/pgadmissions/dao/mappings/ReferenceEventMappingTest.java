package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.CountriesDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceEvent;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RefereeBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class ReferenceEventMappingTest extends AutomaticRollbackTestCase {
	

	private Referee referee;
	private RegisteredUser user;
	@Test
	
	public void shouldSaveAndLoadReferenceEvent() throws ParseException {	
		
		Date eventDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 12 2011 14:09:26");
		Event event = new ReferenceEventBuilder().date(eventDate).user(user).referee(referee).toEvent();
		sessionFactory.getCurrentSession().saveOrUpdate(event);
		assertNotNull(event.getId());
		ReferenceEvent reloadedEvent = (ReferenceEvent) sessionFactory.getCurrentSession().get(ReferenceEvent.class, event.getId());
		assertSame(event, reloadedEvent);

		flushAndClearSession();
		reloadedEvent = (ReferenceEvent) sessionFactory.getCurrentSession().get(ReferenceEvent.class, event.getId());
		assertNotSame(event, reloadedEvent);
		assertEquals(event.getId(), reloadedEvent.getId());

		assertEquals(eventDate, reloadedEvent.getDate());		
		assertEquals(user.getId(), reloadedEvent.getUser().getId());
		assertEquals(referee.getId(), reloadedEvent.getReferee().getId());

	}
	@Before
	public void setup() {
		user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
				.accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();		
		

		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).toApplicationForm();
		CountriesDAO countriesDAO = new CountriesDAO(sessionFactory);
		 referee = new RefereeBuilder().application(application).addressCountry(countriesDAO.getCountryById(1))
				.address1("loc").email("email").firstname("name").jobEmployer("emplo").jobTitle("titl").lastname("lastname")
				.phoneNumber("phoneNumber").toReferee();
		
		
		save(user, program,  application, referee);

		flushAndClearSession();
	}
	
}
