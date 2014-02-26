package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.StateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class StateChangeEventMappingTest extends AutomaticRollbackTestCase {
	
	@Test
	public void shouldSaveAndLoadStateChangeEvent() throws ParseException {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		save(user);
		flushAndClearSession();
		
		ApplicationFormStatus newStatus = ApplicationFormStatus.APPROVAL;
		Date eventDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 12 2011 14:09:26");
		Event event = new StateChangeEventBuilder().newStatus(newStatus).date(eventDate).user(user).build();
		sessionFactory.getCurrentSession().saveOrUpdate(event);
		assertNotNull(event.getId());
		StateChangeEvent reloadedEvent = (StateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, event.getId());
		assertSame(event, reloadedEvent);

		flushAndClearSession();
		reloadedEvent = (StateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, event.getId());
		assertNotSame(event, reloadedEvent);
		assertEquals(event.getId(), reloadedEvent.getId());

		assertEquals(eventDate, reloadedEvent.getDate());
		assertEquals(newStatus, reloadedEvent.getNewStatus());
		assertEquals(user.getId(), reloadedEvent.getUser().getId());
	}

	@Test
	public void shouldLoadApplicationFormForStateChangeEvent() throws ParseException {
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a47").domicileCode("AE").enabled(true).build();
		Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();

		save(applicant, institution, program);
		ApplicationFormStatus newStatus = ApplicationFormStatus.APPROVAL;
		Date eventDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 12 2011 14:09:26");
		StateChangeEvent event = new StateChangeEventBuilder().newStatus(newStatus).date(eventDate).build();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).events(event).build();

		save(application);
		flushAndClearSession();
		StateChangeEvent reloadedEvent = (StateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, event.getId());

		assertEquals(application.getId(), reloadedEvent.getApplication().getId());
	}
}
