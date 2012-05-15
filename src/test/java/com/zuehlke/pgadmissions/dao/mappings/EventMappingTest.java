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
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.EventBuilder;
import com.zuehlke.pgadmissions.domain.builders.NotificationRecordBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.NotificationType;

public class EventMappingTest extends AutomaticRollbackTestCase {
	@Test
	public void shouldSaveAndLoadEvent() throws ParseException {

		ApplicationFormStatus newStatus = ApplicationFormStatus.APPROVAL;
		Date eventDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 12 2011 14:09:26");
		Event event = new EventBuilder().newStatus(newStatus).date(eventDate).toEvent();
		sessionFactory.getCurrentSession().saveOrUpdate(event);
		assertNotNull(event.getId());
		Event reloadedEvent = (Event) sessionFactory.getCurrentSession().get(Event.class, event.getId());
		assertSame(event, reloadedEvent);

		flushAndClearSession();
		reloadedEvent = (Event) sessionFactory.getCurrentSession().get(Event.class, event.getId());
		assertNotSame(event, reloadedEvent);
		assertEquals(event, reloadedEvent);

		assertEquals(eventDate, reloadedEvent.getDate());
		assertEquals(newStatus, reloadedEvent.getNewStatus());

	}

	@Test
	public void shouldLoadApplicationFormForEvent() throws ParseException {
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();

		Program program = new ProgramBuilder().code("doesntexist").title("another title").toProgram();

		save(applicant, program);
		ApplicationFormStatus newStatus = ApplicationFormStatus.APPROVAL;
		Date eventDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 12 2011 14:09:26");
		Event event = new EventBuilder().newStatus(newStatus).date(eventDate).toEvent();
		ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(applicant).events(event).toApplicationForm();

		save(application);
		flushAndClearSession();
		Event reloadedEvent = (Event) sessionFactory.getCurrentSession().get(Event.class, event.getId());

		assertEquals(application, reloadedEvent.getApplication());

	}
}
