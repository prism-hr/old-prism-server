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

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.ApprovalStateChangeEvent;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApprovalStateChangeEventMappingTest extends AutomaticRollbackTestCase {
	
    private ApprovalRound approvalRound;
	
	@Test
	public void shouldSaveAndLoadApprovalStateChangeEvent() throws ParseException {
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("otheremail@test.com").username("username1").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        save(user);
        flushAndClearSession();
		
		ApplicationFormStatus newStatus = ApplicationFormStatus.APPROVAL;
		Date eventDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 12 2011 14:09:26");
		Event event = new ApprovalStateChangeEventBuilder().newStatus(newStatus).date(eventDate).user(user).approvalRound(approvalRound).build();
		sessionFactory.getCurrentSession().saveOrUpdate(event);
		assertNotNull(event.getId());
		ApprovalStateChangeEvent reloadedEvent = (ApprovalStateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, event.getId());
		assertSame(event, reloadedEvent);

		flushAndClearSession();
		reloadedEvent = (ApprovalStateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, event.getId());
		assertNotSame(event, reloadedEvent);
		assertEquals(event.getId(), reloadedEvent.getId());
		assertEquals(eventDate, reloadedEvent.getDate());
		assertEquals(newStatus, reloadedEvent.getNewStatus());
		assertEquals(user.getId(), reloadedEvent.getUser().getId());
		assertEquals(approvalRound.getId(), reloadedEvent.getApprovalRound().getId());
	}
	
	@Before
	public void prepare() {
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").build();
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();
        approvalRound = new ApprovalRoundBuilder()
                .supervisors(new SupervisorBuilder().user(user).isPrimary(false).build()).application(application)
                .build();
        save(user, program, application, approvalRound);
        flushAndClearSession();
	}
	
}
