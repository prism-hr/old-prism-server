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
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.ReviewStateChangeEvent;
import com.zuehlke.pgadmissions.domain.StateChangeEvent;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewStateChangeEventBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ReviewStateChangeEventMappingTest extends AutomaticRollbackTestCase {
	

	private ReviewRound reviewRound;
	@Test
	public void shouldSaveAndLoadReviewStateChangeEvent() throws ParseException {
		RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("otheremail@test.com").username("username1")
				.password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
		save(user);
		flushAndClearSession();
		
		ApplicationFormStatus newStatus = ApplicationFormStatus.APPROVAL;
		Date eventDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 12 2011 14:09:26");
		Event event = new ReviewStateChangeEventBuilder().newStatus(newStatus).date(eventDate).user(user).reviewRound(reviewRound).build();
		sessionFactory.getCurrentSession().saveOrUpdate(event);
		assertNotNull(event.getId());
		ReviewStateChangeEvent reloadedEvent = (ReviewStateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, event.getId());
		assertSame(event, reloadedEvent);

		flushAndClearSession();
		reloadedEvent = (ReviewStateChangeEvent) sessionFactory.getCurrentSession().get(StateChangeEvent.class, event.getId());
		assertNotSame(event, reloadedEvent);
		assertEquals(event.getId(), reloadedEvent.getId());
		assertEquals(eventDate, reloadedEvent.getDate());
		assertEquals(newStatus, reloadedEvent.getNewStatus());
		assertEquals(user.getId(), reloadedEvent.getUser().getId());
		assertEquals(reviewRound.getId(), reloadedEvent.getReviewRound().getId());
	}
	
	@Before
	public void prepare() {
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
        ApplicationForm application = new ApplicationFormBuilder().advert(testObjectProvider.getEnabledProgram()).applicant(user).build();
        reviewRound = new ReviewRoundBuilder().reviewers(new ReviewerBuilder().user(user).build())
                .application(application).build();

        save(user, application, reviewRound);

        flushAndClearSession();
	}
	
}
